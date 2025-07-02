<template>
    <div>
        <!-- 
            使用 v-model 绑定 inputPage 响应式值，
            采用 number 可自动的将 inputPage.value 转换成数字。
        -->
        <input v-model.number="inputPage" 
              type="number" placeholder="page: "
              min="1">
        <button @click="queryWithInputPage()" :disabled="isInputEmpty()">Query</button>
    </div>
    <div class="score-record-container">
        <!-- 加载状态 -->
        <div v-if="loading" class="loading">加载中...</div>
        
        <!-- 错误提示 -->
        <div v-if="error" class="error">{{ error }}</div>
        
        <!-- 数据展示 -->
        <div v-if="paginationScoreData" class="score-record-details">
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
                    <tr v-for="(score, index) in paginationScoreData">
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
    <div v-if="metadata" class="pagination-info">
        <p>
            一页 {{metadata.pagination.size}} 条数据，
            当前处于第 {{metadata.pagination.page}} 页，
            共 {{metadata.pagination.totalItem / metadata.pagination.size}} 页
        </p>
    </div>
    <div v-if="metadata" class="navigation">
        <button
            @click="fetchPaginationRecord(
                FormatUtils.extractIDFromURL(
                    metadata._links.prev_page.href, 'page'
                ))" :disabled="!metadata._links.prev_page">
            前一页
        </button>
        <button
            @click="fetchPaginationRecord(
                FormatUtils.extractIDFromURL(
                    metadata._links.next_page.href, 'page'
                ))" :disabled="!metadata._links.next_page">
            下一页
        </button>
        <button
            @click="fetchPaginationRecord(
                FormatUtils.extractIDFromURL(
                    metadata._links.first_page.href, 'page'
                ))" :disabled="!metadata._links.first_page">
            第一页
        </button>
        <button
            @click="fetchPaginationRecord(
                FormatUtils.extractIDFromURL(
                    metadata._links.last_page.href, 'page'
                ))" :disabled="!metadata._links.last_page">
            最后一页
        </button>
    </div>
</template>

<style scoped>
/* 基础暗黑主题设置 */
div {
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Helvetica, Arial, sans-serif;
  color: #c9d1d9;
  background-color: #0d1117;
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
}

input[type="number"] {
  flex-grow: 1;
  padding: 8px 12px;
  background-color: #0d1117;
  border: 1px solid #30363d;
  border-radius: 6px;
  color: #c9d1d9;
  font-size: 14px;
  transition: border-color 0.2s ease;
}

input[type="number"]:focus {
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
  transition: background-color 0.2s ease;
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

/* 加载状态样式 */
.loading {
  padding: 20px;
  text-align: center;
  color: #58a6ff;
  font-size: 16px;
  font-weight: 500;
}

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

/* 表格容器样式 */
.score-record-container {
  border: 1px solid #30363d;
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
}
</style>

<script setup>
import { ref, onMounted, watch } from 'vue';
import axios from 'axios';
import { useRoute } from 'vue-router';
import FormatUtils from '../utils/FormatUtils';

/** 从前端传来的 URL 参数之定义。*/
const props = defineProps({
    page: {
        type: [String, Number],
        defult: 1,
        required: true,
        validator: (page) => {
            const pageNumber = Number(page);
            return !isNaN(pageNumber) && pageNumber > 1;
        }
    }
});

const paginationScoreData = ref(null);
const metadata            = ref(null);
const loading             = ref(true);
const error               = ref(null);
const currentPage         = ref(props.page);
const inputPage           = ref('');

const route = useRoute();

/** 
 * 查询指定页码的用户成绩。
 * 
 * 响应 JSON 格式见：
 * https://github.com/JesseZ332623/Spring-Reactor-Program-Practice/blob/master/Server/documents/API%20Documents.md
*/
const fetchPaginationRecord = async (page) =>
{
    try 
    {
        if (isNaN(page) && page <= 0) {
            throw new Error("Invalid page input!");
        }

        loading.value  = true;
        const response = await axios.get(`/api/query/paginate_score?page=${page}`);

        console.info("Response message: ", response.data.message);

        route.meta.title = `Score record page ${page}`;
        document.title   = route.meta.title;

        paginationScoreData.value = response.data.data;
        metadata.value            = response.data.metadata;
        currentPage.value         = page;
        error.value               = null;
    }
    catch (err) 
    {
        route.meta.title = 'Load failed!';
        document.title   = route.meta.title;

        (err.response.data.message) 
			? error.value = `${err.response.data.message}`
			: error.value = err.message;

        console.error(err);
    } 
    finally {
        loading.value = false; // 不论成功与否，都应该关闭加载状态
    }
};

const isInputEmpty = () => {
    return !inputPage.value;
}

const queryWithInputPage = () => 
{
    let pageNumber 
        = Number.parseInt(inputPage.value);

    fetchPaginationRecord(pageNumber);
};

watch(
    () => { props.page; },
    (newPage) => { fetchPaginationRecord(newPage); }
);

onMounted(
    () => 
    fetchPaginationRecord(currentPage.value)
);
</script>