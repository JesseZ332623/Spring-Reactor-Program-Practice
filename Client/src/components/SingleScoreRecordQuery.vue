<template>
	<div>
		<header>
			Single score record query
		</header>
	</div>

   <div class="score-record-container">
		<!-- 加载状态 -->
		<div v-if="loading" class="loading">加载中...</div>
		
		<!-- 错误提示 -->
		<div v-if="error" class="error">{{ error }}</div>
		
		<!-- 数据展示 -->
		<div v-if="record" class="record-details">
		<strong style="color: #ffffff;">成绩记录 #{{ record.scoreId }}</strong>
		
		<div class="user-info">
			<p><strong>用户ID:</strong> {{ record.userId }}</p>
			<p><strong>用户名:</strong> {{ record.userName }}</p>
			<p><strong>提交时间:</strong> {{ record.submitDate }}</p>
		</div>
		
		<div class="stats">
			<div class="stat-item correct">
			<span class="stat-label">正确:</span>
			<span class="stat-value">{{ record.correctCount }}</span>
			</div>
			<div class="stat-item error">
			<span class="stat-label">错误:</span>
			<span class="stat-value">{{ record.errorCount }}</span>
			</div>
			<div class="stat-item no-answer">
			<span class="stat-label">未答:</span>
			<span class="stat-value">{{ record.noAnswerCount }}</span>
			</div>
		</div>
		</div>
		
		<!-- 导航控件 -->
		<div v-if="metadata" class="navigation">
			<!-- 在 VUE 框架中使用 @click 代替 onclick 属性。-->
			<button
				@click="fetchRecord(FormatUtils.extractIDFromURL(metadata._links.prev.href, 'id'))"
				:disabled="!metadata._links.prev">
				上一条
			</button>
			
			<button 
				@click="fetchRecord(FormatUtils.extractIDFromURL(metadata._links.next.href, 'id'))"
				:disabled="!metadata._links.next">
				下一条
			</button>
			
			<button 
				@click="fetchRecord(FormatUtils.extractIDFromURL(metadata._links.first.href, 'id'))"
				:disabled="!metadata._links.first">
				第一条
			</button>

			<button 
				@click="fetchRecord(FormatUtils.extractIDFromURL(metadata._links.last.href, 'id'))"
				:disabled="!metadata._links.last">
				最后一条
			</button>
		</div>
  </div>
</template>

<style scoped>
header {
		text-align: center;
		padding: 1rem;
		background-color: #1890ff;
		color: white;
}

.score-record-container {
  max-width: 800px;
  margin: 2rem auto;
  padding: 1.5rem;
  border: 1px solid #eaeaea;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.loading, .error {
  text-align: center;
  padding: 2rem;
  font-size: 1.2rem;
}

.error {
  color: #ff4d4f;
}

.record-details {
  margin-bottom: 2rem;
}

.user-info p {
  color: #ffffff;
  margin: 0.5rem 0;
}

.stats {
  display: flex;
  justify-content: space-around;
  margin: 2rem 0;
}

.stat-item {
  text-align: center;
  padding: 1rem;
  margin: 0 6px 0 6px;
  border-radius: 8px;
  width: 30%;
}

.stat-label {
  display: block;
  font-weight: bold;
  margin-bottom: 0.5rem;
}

.stat-value {
  font-size: 1.8rem;
  font-weight: bold;
}

.correct { background-color: #238636; border: 1px solid #b7eb8f; }
.error { background-color: #fff2f0; border: 1px solid #ffccc7; }
.no-answer { background-color: #f9f9f9; border: 1px solid #d9d9d9; }

.navigation {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 1.5rem;
  padding-top: 1rem;
  border-top: 1px solid #eee;
}

button {
  padding: 0.5rem 1rem;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

button:hover:not(:disabled) {
  background-color: #40a9ff;
}

button:disabled {
  background-color: #d9d9d9;
  cursor: not-allowed;
}

.current-id {
  font-weight: bold;
}
</style>

<script setup>
import { ref, onMounted, watch} from 'vue';
import axios from 'axios';
import { useRoute } from 'vue-router';
import FormatUtils from '../utils/FormatUtils';

/** 
 * 定义参数属性。
 * 
 * 参数 id 为数字类型，不得为空，否则默认值为 1。
 * 此外提供验证方法，检测该属性是否为数字类型。
*/
const props = defineProps({
  id: {
		type: [String, Number],
		default: 1,
		required: true,
		validator: (value) => {
			const num = Number(value);
			return !isNaN(num) && num > 0;
		}
  	}
});

/**
 * 在传统的原生 JS 中，修改一个变量，倘若不使用：
 * 
 *      document.getElementById(...).textContent = ...;
 * 
 * 这样的方式去手动的更新值，前端页面是不会有响应的。
 * 
 * 而 VUE 框架解决了这一痛点，对于要实时更新的数据，
 * 需要调用 vue 模块中的 ref() 方法对数据进行 “跟踪”。
 * 当一个 ref() 返回的对象被修改时，vue 会检测到并重新渲染与之绑定的 DOM。
*/
const record    = ref(null);    // 对于暂时不知道的数据，可以填 null 参数
const metadata  = ref(null);
const loading   = ref(true);
const error     = ref(null);
const currentID = ref(Number(props.id));

const route = useRoute();

/** 
 * 查询指定 ID 的用户成绩。
 * 
 * 响应 JSON 格式见：
 * https://github.com/JesseZ332623/Spring-Reactor-Program-Practice/blob/master/Server/documents/API%20Documents.md
*/
const fetchRecord = async (id) => 
{
    try 
    {
        loading.value   = true;     // 打开加载状态
        const response  = await axios.get(`/api/query/score_record?id=${id}`); // 发起请求

        console.info("Response: ", response.data.message);
        
        route.meta.title = `Score id = ${response.data.data.scoreId} of ${response.data.data.userName}`;
        document.title   = `${route.meta.title}`;

        record.value     = response.data.data;
        metadata.value   = response.data.metadata;
        currentID.value  = id;
        error.value      = null;
    } 
    catch (err) 
    {
        route.meta.title = 'Load failed!';
		document.title   = route.meta.title;

		(err.response.data.message) 
			? error.value = `${err.response.data.message}`
			: error.value = err.message; 
	
        console.error(error.value);
    } 
    finally {
        loading.value = false; // 不论成功与否，都应该关闭加载状态
    }
};

/** 
 * 监听 props.id 的变化，
 * 若出现变化更新 currentID 并请求新的数据。
*/
watch(
	() => { props.id; },
  	(newId) => fetchRecord(newId)
);

/** 组件挂载时获取初始数据。 */
onMounted(() => { 
    fetchRecord(currentID.value); 
});
</script>