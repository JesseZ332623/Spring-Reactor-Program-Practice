## Flux 的冷门操作符

1.  **`bufferTimeout(int maxSize, Duration maxTime)`**:
    *   **作用**: 组合了 `buffer(int maxSize)` 和 `bufferTimeout(Duration maxTime)`。当元素数量达到 `maxSize` **或** 时间间隔达到 `maxTime` 时（**先触发哪个条件就发射哪个**），就会发射一个包含当前累积元素的列表。
    *   **场景**: 需要同时考虑缓冲数量和缓冲时间上限的场景。例如，收集日志或指标，既要避免列表过大（内存），也要避免等待时间过长（延迟），满足任一条件就发送一批。比单独使用 `buffer(int)` 或 `buffer(Duration)` 更灵活地控制背压和延迟。

2.  **`collectMultimap(Function keySelector)` / `collectMultimap(Function keySelector, Function valueTransformer)`**:
    *   **作用**: 将流中的所有元素收集到一个 `MultiMap` (本质是 `Map<K, Collection<V>>`) 中，并通过 `Mono<Map<K, Collection<V>>>` 返回。`keySelector` 用于生成键，可选的 `valueTransformer` 用于转换值。
    *   **场景**: 需要根据某个键对元素进行分组收集，且同一个键可能对应多个值。比 `groupBy` + `collectList` 更简洁地一步到位得到一个完整的分组映射。例如，将订单流按用户ID分组收集所有订单项。

3.  **`distinctUntilChanged()`**:
    *   **作用**: 只过滤掉**连续重复**的元素。与 `distinct()` (过滤所有重复值) 不同，它允许相同的值在非连续位置出现。
    *   **场景**: 传感器数据流中，只关心数值**变化**的时刻，忽略连续相同的读数。处理用户输入事件流，避免快速重复点击触发多次操作（需要结合 `debounce` 等使用效果更佳）。日志流中压缩连续的相同错误信息。

4.  **`elapsed()`**:
    *   **作用**: 将每个元素转换为一个 `Tuple2<Long, T>`，其中 `Long` 表示当前元素与前一个元素之间的时间间隔（以毫秒为单位，从订阅开始算起）。
    *   **场景**: 需要精确测量元素发射间隔时间的性能分析、监控或调试。例如，分析事件总线的吞吐量波动。

5.  **`index()`**:
    *   **作用**: 将每个元素转换为一个 `Tuple2<Long, T>`，其中 `Long` 是从 0 开始的序列号（索引）。
    *   **场景**: 需要知道元素在流中绝对位置的场景。例如，在处理分页结果流时标记序号，或者在批处理中记录进度。`zipWithIndex()` 也能达到类似效果。

6.  **`limitRequest(long n)`**:
    *   **作用**: 限制**下游**从该 `Flux` **请求**的元素总数量为 `n`。**不是**限制源头的生产，而是限制消费者的消费量。达到限制后，会发送 `onComplete`。
    *   **场景**: 强制下游只消费前 N 个元素，无论它实际请求多少。用于在组合操作链中精确控制消费量，或者在测试中模拟下游只请求部分数据。与 `take(long n)` (主动取前N个) 的关注点不同。

7.  **`transformDeferred(Function<Flux, Publisher> transformer)`**:
    *   **作用**: 与 `transform` 类似，在组装期应用转换函数。但关键区别在于 `transformDeferred` 中的 `transformer` 函数是在**每个订阅时**被调用。这允许基于每个订阅的上下文（如 `Context`）动态构建操作链。
    *   **场景**: 需要为每个订阅者动态创建不同的操作链（例如，基于认证信息应用不同的过滤或映射逻辑）。是实现更高级、上下文感知的动态流处理的基础。

8.  **`windowTimeout(int maxSize, Duration maxTime)`**:
    *   **作用**: 类似于 `bufferTimeout`，但发射的是 `Flux<Flux<T>>`。每当元素数量达到 `maxSize` **或** 时间间隔达到 `maxTime` 时（先触发哪个），就会打开一个新的“窗口” `Flux` 来收集后续元素，并关闭前一个窗口。
    *   **场景**: 需要将无限流切割成有限的时间窗口或大小窗口进行处理，并且希望以流的形式处理每个窗口（例如，对每个窗口进行聚合计算）。是 `bufferTimeout` 的“窗口”视图版本。

## Mono 的冷门操作符

1.  **`cache(Duration ttl)`**:
    *   **作用**: 缓存 `Mono` 的结果（成功值或错误），并在 `ttl` (Time-To-Live) 时间内，让后续的所有订阅者立即获得这个缓存的结果。超过 `ttl` 后，下一个订阅者会触发重新计算。`cache()` (无参数) 会永久缓存。
    *   **场景**: 需要短期缓存昂贵操作（如网络请求、复杂计算）的结果，避免在短时间内重复执行。**需谨慎使用**，容易造成状态陈旧或内存泄漏。通常 `Mono.cache()` 或 `cache(Duration)` 比 `Flux.cache` 更安全一些，因为 `Mono` 只发射一次。

2.  **`deferContextual(Function<ContextView, Mono> contextualMonoSupplier)`**:
    *   **作用**: 高阶操作符。延迟创建实际的 `Mono`，直到有订阅发生，并且在创建时可以访问到当前订阅的 `Context`。
    *   **场景**: `Mono` 的创建或其行为**强烈依赖**于 Reactor `Context` 中的信息（如认证 Token、请求 ID、跟踪信息）。是实现上下文感知的 `Mono` 源的关键。例如，根据 `Context` 中的用户信息动态决定返回什么数据。

3.  **`materialize()`** / **`dematerialize()`**:
    *   **作用**:
        *   `materialize()`: 将 `Mono` 发出的信号（`onNext` + 值, `onComplete`, `onError` + 异常）都转换为一个 `Signal` 对象，包装在 `Mono<Signal<T>>` 中发射出来。流本身不会发出 `onComplete` 或 `onError`（除非转换过程出错）。
        *   `dematerialize()`: 是 `materialize()` 的反操作。期望一个 `Mono<Signal<T>>`，并将其还原为原始的 `Mono<T>` 行为，根据 `Signal` 的类型发出相应信号。
    *   **场景**: 需要显式处理或检查流的信号本身（而不仅仅是值或错误），例如在自定义的日志记录、审计、错误处理中间件或操作符中。或者需要将信号作为数据传递并在另一个地方“回放”。

4.  **`onTerminateDetach()`**:
    *   **作用**: 让 `Mono` 在终止（`onComplete` 或 `onError`）后，主动断开与上游源和下游订阅者的引用联系，帮助垃圾回收。
    *   **场景**: 处理生命周期非常长或可能被不恰当持有的 `Mono`，尤其是在它已经完成很久之后，为了预防潜在的内存泄漏。这是一种防御性的资源管理手段。

5.  **`usingWhen(Publisher resourceSupplier, Function resourceClosure, Function asyncCleanup)`**:
    *   **作用**: 一个更强大但也更复杂的资源管理操作符。用于管理需要在 `Mono` 生命周期（订阅、完成、错误、取消）中获取和清理的资源（尤其是异步资源）。
        *   `resourceSupplier`: 提供资源的 `Publisher` (通常是 `Mono` 或 `Flux` 只发一个)。
        *   `resourceClosure`: 函数，接收资源并生成实际的主业务 `Mono<T>`。
        *   `asyncCleanup`: 函数，接收资源和一个表示终止信号的 `Publisher` (`Mono`)，返回一个 `Publisher` (`Mono`) 来执行异步清理。这个函数会在主 `Mono` 终止（成功、错误）**或** 订阅被取消时调用。
    *   **场景**: 管理需要复杂、异步清理逻辑的资源（如数据库连接、网络连接、文件句柄等）。它提供了对取消信号和不同终止原因（完成、错误）的细粒度控制。比 `Mono.using` / `Flux.using` 更强大，但也更复杂。

6.  **`hide()`**:
    *   **作用**: 创建一个行为完全相同但隐藏了原始 `Mono` 具体实现类型（以及可能暴露的内部状态）的新 `Mono`。主要用于库作者防止用户依赖内部实现细节。
    *   **场景**: 当作为库提供者返回一个 `Mono` 时，为了保护内部实现不被使用者通过 `instanceof` 或强制转换进行不安全的操作或依赖。