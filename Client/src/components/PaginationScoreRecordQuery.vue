<template>
	<h1 class="title">Pagination Query</h1>
	<!-- 错误提示 -->
		<div v-if="error" class="error">{{ error }}</div>

    <div class="score-record-container">
		<div>
			<!-- 
				使用 v-model 绑定指定的响应式值，
				采用 v-model.number 可自动的将指定响应式值转换成数字。
			-->
			<input v-model="inputName"
				type="text" placeholder="name: ">
			<input v-model.number="inputPage" 
				type="number" placeholder="page: " min="1">
			<button @click="doPaginationQuery()" :disabled="isInputEmpty() || loading">
				{{ loading ? '加载中...' : 'Query' }}
			</button>
    	</div>
		<!-- 加载提示 -->
		<div v-if="loading" class="loading">
			<!-- <div class="loader"></div>  -->
			<p>Loading...</p>
		</div>
		<transition name="fade" mode="out-in">
			<div :key="tableKey">
				<!-- 数据展示 -->
				<div v-if="!loading && paginationScoreData" class="score-record-details">
					<table class="score-record-table">
						<caption>Pagination score record query</caption>
						<thead>
							<tr>
								<th scope="col">Score ID</th>
								<th scope="col">User ID</th>
								<th scope="col">User Name</th>
								<th scope="col">Correct</th>
								<th scope="col">Error</th>
								<th scope="col">No Answer</th>
								<th scope="col">Submit Date</th>
							</tr>
						</thead>
						<tbody>
							<tr v-for="(score, index) in paginationScoreData" 
							   :key="score.scoreId + '-' + index" class="row-item">
								<th scope="row">{{score.scoreId}}</th>
								<th scope="row">{{score.userId}}</th>
								<th scope="row">{{score.userName}}</th>
								<td>{{score.correctCount}}</td>
								<td>{{score.errorCount}}</td>
								<td>{{score.noAnswerCount}}</td>
								<td>{{score.submitDate}}</td>
							</tr>
						</tbody>
					</table>
				</div>
			</div>
		</transition>
		<div v-if="metadata" class="pagination-info">
			<p>
				一页 {{metadata.pagination.size}} 条数据，
				当前处于第 {{metadata.pagination.page}} 页，
				共 {{pageAmount}} 页。
			</p>
		</div>
    </div>
    <div v-if="metadata" class="navigation">
        <button
            @click="gotoPage('prev')" 
			:disabled="!metadata._links.prev_page || loading">
            上一页
        </button>
        <button
            @click="gotoPage('next')" 
			:disabled="!metadata._links.next_page || loading">
            下一页
        </button>
        <button
            @click="gotoPage('first')" 
			:disabled="!metadata._links.first_page || loading">
            第一页
        </button>
        <button
            @click="gotoPage('last')" 
			:disabled="!metadata._links.last_page || loading">
            最后一页
        </button>
    </div>
</template>

<style scoped>
/* 新增过渡动画 */
.fade-enter-active, .fade-leave-active { 
	transition: opacity 0.2s ease, 
			    transform 0.2s ease;
}

.fade-enter-from, .fade-leave-to {
  opacity: 0;
  transform: translateY(10px);
}

/* 添加行级动画 */
.row-item {
  animation: rowEntry 0.2s ease-out forwards;
  opacity: 0;
}

/* 基础暗黑主题设置 */
div {
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Helvetica, Arial, sans-serif;
  color: #c9d1d9;
  background-color: #0d1117;
}

.title {
	font-size: 35px;
	margin-bottom: 40px;
}

/* 输入和按钮区域样式 */
div:first-child {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  padding: 15px;
  background-color: #161b22;
  border-radius: 6px;
  border: 1px solid #30363d;
  animation: tableFade 0.4s ease-in;
}

input[type="text"] {
  flex-grow: 1;
  padding: 8px 12px;
  background-color: #0d1117;
  border: 1px solid #30363d;
  border-radius: 6px;
  color: #c9d1d9;
  font-size: 15px;
}

input[type="number"] {
  flex-grow: 1;
  padding: 8px 12px;
  background-color: #0d1117;
  border: 1px solid #30363d;
  border-radius: 6px;
  color: #c9d1d9;
  font-size: 14px;
}

input:focus {
  outline: none;
  border-color: #58a6ff;
  box-shadow: 0 0 0 3px rgba(17, 88, 199, 0.3);
}

button {
  padding: 8px 16px;
  background-color: #238636;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
}

button:hover:not(:disabled) {
  background-color: #2ea043;
}

button:active:not(:disabled) {
  background-color: #238636;
}

button:disabled {
  background-color: #484f58;
  cursor: not-allowed;
  opacity: 0.7;
}

.loading {
  padding: 20px;
  text-align: center;
  color: #58a6ff;
  font-size: 16px;
  font-weight: 500;
}

/* 添加加载动画
.loader {
  border: 3px solid rgba(88, 166, 255, 0.2);
  border-top: 3px solid #58a6ff;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  animation: spin 1s linear infinite;
  margin: 0 auto 10px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
} */

/* 错误提示样式 */
.error {
  padding: 15px;
  background-color: rgba(248,81,73,0.15);
  border: 1px solid rgba(248,81,73,0.4);
  border-radius: 6px;
  color: #f85149;
  margin: 15px 0;
  font-weight: 500;
}

.score-record-details {
  will-change: transform, opacity;
}

/* 表格容器样式 */
.score-record-container {
  /* border: 1px solid #30363d; */
  border-radius: 6px;
  overflow: hidden;
}

/* 表格样式 */
.score-record-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.score-record-table caption {
  padding: 12px 0;
  font-size: 16px;
  font-weight: 600;
  background-color: #161b22;
  border-bottom: 1px solid #30363d;
}

.score-record-table th {
  background-color: #161b22;
  font-weight: 600;
  text-align: left;
  padding: 12px 16px;
  border-bottom: 1px solid #21262d;
}

.score-record-table td {
  padding: 12px 16px;
  border-bottom: 1px solid #21262d;
}

.score-record-table tbody tr {
  transition: background-color 0.15s ease;
}

.score-record-table tbody tr:hover {
  background-color: rgba(56, 139, 253, 0.1);
}

.score-record-table tbody tr:last-child td {
  border-bottom: none;
}

.navigation {
    margin-top: 15px;
}

.navigation button {
    margin-left: 10px;
    margin-right: 10px;
}

/* 动画关键帧，表格整体从下到上的移动。 */
@keyframes tableFade {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 动画关键帧，表格行从左到右的移动。 */
@keyframes rowEntry {
  from {
    opacity: 0;
    transform: translateX(-8px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .score-record-table {
    display: block;
    overflow-x: auto;
  }
  
  div:first-child {
    flex-direction: column;
  }
  
  input[type="number"] {
    width: 100%;
  }

  .navigation {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
  }

  .navigation button {
    margin: 5px;
    flex: 1 0 40%;
  }
}
</style>

<script setup>
import { ref, computed } from 'vue';
import axios from 'axios';
import { useRoute } from 'vue-router';
import FormatUtils from '../utils/FormatUtils';

const paginationScoreData = ref(null);
const metadata            = ref(null);
const error               = ref(null);
const loading			  = ref(false);
const inputName			  = ref('');
const inputPage           = ref('');
const pageAmount          = ref(0);

const currentQueryName    = ref(''); 
const currentQueryPage    = ref(1);

const lastUpdateTimestamp = ref(Date.now());

const route = useRoute();

const tableKey = computed(() => {
    return `${currentQueryName.value}_${currentQueryPage.value}_${Date.now()}`;
});

/** 
 * 查询指定页码的用户成绩。
 * 
 * 响应 JSON 格式见：
 * @see https://github.com/JesseZ332623/Spring-Reactor-Program-Practice/blob/master/Server/documents/API%20Documents.md
*/
const fetchPaginationRecord = async (name, page) =>
{
    try 
    {
        if (isNaN(page) && page <= 0) {
            throw new Error("Invalid page input!");
        }

		loading.value = true;

        const response 
			= await axios.get(`/api/query/paginate_score?name=${name}&page=${page}`);

        console.info("Response message: ", response.data.message);

        route.meta.title = `Score record of ${name} page ${page}`;
        document.title   = route.meta.title;

        paginationScoreData.value = response.data.data;
        metadata.value            = response.data.metadata;

		console.info("Meta data: ", metadata.value);

        if (metadata.value) 
		{
			const total = metadata.value.pagination.totalItem;
			const size  = metadata.value.pagination.size;
			
			// 处理除数为 0 的情况
			pageAmount.value = (size > 0) ? Math.ceil(total / size) : 0;
    	}

		currentQueryName.value = name;
		currentQueryPage.value = page;

		lastUpdateTimestamp.value = Date.now();
		
        error.value = null;
    }
    catch (err) 
    {
        route.meta.title = 'Load failed!';
        document.title   = route.meta.title;

		paginationScoreData.value = null;

        (err.response.data.message) 
			? error.value = `${err.response.data.message}`
			: error.value = err.message;

        console.error(err);
    }
	finally 
	{
		// 添加短暂延迟确保动画可见
        setTimeout(() => {
            loading.value = false;
        }, 200);
	}
};

/** 只有在 name 和 page 都有输入时，才开放查询。*/
const isInputEmpty = () => {
    return !inputPage.value && !inputName.value;
}

/** 获取输入框的参数，执行分页查询。*/
const doPaginationQuery = () => 
{
    let pageNumber = Number.parseInt(inputPage.value);

	if (pageNumber < 1) {
		error.value = 'Page number could not less than 1!';
	}

    fetchPaginationRecord(inputName.value, pageNumber);
};

/** 提取 HATEOAS 链接参数，执行分页查询。 */
const gotoPage = (direction) => 
{
	if (!metadata.value || loading.value) { return; }

	let page;

	switch (direction) 
	{
		case 'prev':
			page = FormatUtils.extractIDFromURL(
				metadata.value._links.prev_page.href, 'page'
			);
			break;

		case 'next': 
			page = FormatUtils.extractIDFromURL(
				metadata.value._links.next_page.href, 'page'
			);
			break;

		case 'first': 
			page = FormatUtils.extractIDFromURL(
				metadata.value._links.first_page.href, 'page'
			);
			break;

		case 'last': 
			page = FormatUtils.extractIDFromURL(
				metadata.value._links.last_page.href, 'page'
			);
			break;

		default: break;
	}

	fetchPaginationRecord(
		currentQueryName.value,page
	);
}
</script>