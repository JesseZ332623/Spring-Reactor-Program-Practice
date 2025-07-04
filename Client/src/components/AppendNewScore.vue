<template>
<div class="form-container">
    <!-- 用户 ID 输入 -->
    <div class="input-group">
        <span>User ID: </span>
        <input v-model.number="submitData.userId" 
            type="number" min="1"
            placeholder="user id: ">
    </div>

    <!-- 正确数量输入 -->
    <div class="input-group">
        <span>Correct Count: </span>
        <input v-model.number="submitData.correctCount" 
            type="number" min="0"
            placeholder="correct count: ">
    </div>

    <!-- 错误数量输入  -->
    <div class="input-group">
        <span>Error Count: </span>
        <input v-model.number="submitData.errorCount" 
            type="number" min="0"
            placeholder="error count: ">
    </div>

    <!-- 未回答数量输入 -->
    <div class="input-group">
        <span>No Answer Count: </span>
        <input v-model.number="submitData.noAnswerCount" 
            type="number" min="0"
            placeholder="no answer count: ">
    </div>
    
    <!-- 提交 -->
    <div class="button-group">
        <button @click="doNewScoreAppend()"
                :disabled="isInputDataInValid || isSubmitting"
                type="button">
            {{ isSubmitting ? 'Submitting...' : 'Add New Score' }}
        </button>
    </div>
</div>

<div v-if="dataValidation.message" class="error-message">
    {{ dataValidation.message }}
</div>

<!-- 新数据展示表格 -->
<div v-if="serverResponseData">
    <table>
        <caption>New score preview: </caption>
        <tbody>
            <tr>
                <th scope="row">Score ID</th>
                <td>{{ serverResponseData.scoreId }}</td>
            </tr>
            <tr>
                <th scope="row">User ID</th>
                <td>{{ serverResponseData.userId }}</td>
            </tr>
            <tr>
                <th scope="row">Submit Date</th>
                <td>{{ serverResponseData.submitDate }}</td>
            </tr>
            <tr>
                <th scope="row">Correct Count</th>
                <td>{{ serverResponseData.correctCount }}</td>
            </tr>
            <tr>
                <th scope="row">Error Count</th>
                <td>{{ serverResponseData.errorCount }}</td>
            </tr>
            <tr>
                <th scope="row">No Answer Count</th>
                <td>{{ serverResponseData.noAnswerCount }}</td>
            </tr>
        </tbody>
    </table>
</div>
</template>

<style scoped>
.form-container {
    max-width: 1000px;
    margin: 0 auto;
    padding: 1.5rem;
    background-color: #0d1117;
    border: 1px solid #30363d;
    border-radius: 6px;
    color: #c9d1d9;
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 1.5rem;
}

.input-group {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
    margin-bottom: 0;
}

.input-group span {
    font-size: 0.9rem;
    color: #8b949e;
    min-width: 120px;
    display: inline-block;
}

input {
    width: 100%;
    padding: 0.5rem;
    background-color: #0d1117;
    border: 1px solid #30363d;
    border-radius: 6px;
    color: #c9d1d9;
    font-size: 0.9rem;
    transition: border-color 0.2s;
    box-sizing: border-box;
}

input:focus {
    outline: none;
    border-color: #58a6ff;
    box-shadow: 0 0 0 3px rgba(17, 88, 199, 0.3);
}

input[type="number"]::-webkit-inner-spin-button,
input[type="number"]::-webkit-outer-spin-button {
    -webkit-appearance: none;
    margin: 0;
}

.button-group {
    grid-column: span 2;
    display: flex;
    justify-content: center;
    margin-top: 0.5rem;
}

button {
    background-color: #238636;
    color: white;
    border: none;
    border-radius: 6px;
    padding: 0.6rem 1.2rem;
    font-size: 0.9rem;
    font-weight: 500;
    cursor: pointer;
    transition: background-color 0.2s;
}

button:hover:not(:disabled) {
    background-color: #2ea043;
}

button:disabled {
    background-color: #238636;
    opacity: 0.5;
    cursor: not-allowed;
}

div[v-if="dataValidation.message"] {
    grid-column: span 2;
    margin-top: 1rem;
    padding: 0.75rem;
    background-color: rgba(248, 81, 73, 0.15);
    border: 1px solid rgba(248, 81, 73, 0.4);
    border-radius: 6px;
    color: #f85149;
    font-size: 0.85rem;
}

/* 表格容器跨两列 */
div[v-if="serverResponseData"] {
    grid-column: span 2;
    margin-top: 1rem;
}

table {
    width: 100%;
    margin-top: 2rem;
    border-collapse: collapse;
    border: 1px solid #30363d;
    border-radius: 6px;
    overflow: hidden;
}

caption {
    text-align: left;
    padding: 0.75rem;
    font-size: 0.9rem;
    color: #8b949e;
    background-color: #161b22;
    border-bottom: 1px solid #30363d;
}

th, td {
    padding: 0.75rem;
    text-align: left;
    border-bottom: 1px solid #30363d;
}

th {
    background-color: #161b22;
    color: #8b949e;
    font-weight: 600;
    width: 30%;
}

td {
    background-color: #0d1117;
    color: #c9d1d9;
}

tr:last-child th,
tr:last-child td {
    border-bottom: none;
}

.error-message {
    margin-top: 25px;
    width: 100%;
    color: red;
}
</style>

<script setup>
import { ref, computed, reactive } from 'vue';
import { useRoute } from 'vue-router';
import axios        from 'axios';
import FormatUtils  from '../utils/FormatUtils';

const submitData = reactive({
    userId:         null,       // 用户 ID
    submitDate:     null,       // 提交日期，在发起请求前设置
    correctCount:   null,       // 答对数
    errorCount:     null,       // 错误数
    noAnswerCount:  null        // 未答数
});

const dataValidation = reactive({
    userId:         false,
    submiteData:    false,
    correctCount:   false,
    errorCount:     false,
    noAnswerCount:  false,
    message:        ''
});

const isSubmitting       = ref(false);
const serverResponseData = ref(null);

const requiredFields = ['userId', 'correctCount', 'errorCount', 'noAnswerCount'];

const route = useRoute();

/** 遍历 submiteData，输入的数据是否皆有效？ */
const isInputDataInValid = computed(() =>
{
    // 字段不得为 空，空字符或者 NAN
    return requiredFields.some((field) => {
        return submitData[field] === null ||
               submitData[field] === ''   ||
               isNaN(submitData[field]);
    });
});

/** 验证数据的格式，记录结果到 dataValidation 中去。 */
const validateForm = () => 
{
    let isValid = true;
    dataValidation.message = '';

    // 重置验证结果
    Object.keys(dataValidation).forEach(
        (key) => { 
            if (key !== 'message') { 
                dataValidation[key] = false; 
            } 
        }
    );

    if (!submitData.userId || submitData.userId < 1)
    {
        dataValidation.userId  = true;
        dataValidation.message = 'User ID must be at least 1!';
        isValid = false;
    }

    // 验证计数字段（允许0值）
    const countFields = ['correctCount', 'errorCount', 'noAnswerCount'];
    for (const field of countFields) 
    {
        if (submitData[field] === null || submitData[field] < 0) 
        {
            dataValidation[field]  = true;
            dataValidation.message = 'Count values cannot be negative!';
            isValid = false;
            break;
        }
    }

    return isValid;
};

/** 重置表单操作 */
const resetForm = () => 
{
    Object.keys(submitData).forEach(
        (key) => { submitData[key] = null; }
    );

    Object.keys(dataValidation).forEach(
        (key) => { dataValidation[key] = false; }
    );
};

/** 正式执行新成绩追加操作。 */
const doNewScoreAppend = async () => 
{
    if (!validateForm()) { return; }

    isSubmitting.value = true;

    // "2025-07-04 09:39:14.064"
    submitData.submitDate 
            = new Date().toISOString()
                        .replace('T', ' ').split('.')[0];

    const payload = { ...submitData };

    console.info("Ready to append data!");
    console.info("Data: ", payload);
    
    try 
    {
        const response = await axios.post('/api/add_new_score', payload);

        console.info(response.data.message);
        console.info(response.data);

        serverResponseData.value = response.data.data;

        resetForm();
    }
    catch(error) 
    {
        console.error(
            'Append new score failed! Cause: ',
            error.message
        );

        dataValidation.message 
            = `Append new score failed! Cause: ${error.message}`;
    }
    finally { 
        isSubmitting.value = false; 
    }
};
</script>