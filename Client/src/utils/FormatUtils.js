/** 字符串填充操作。 */
const padStartStr = (number) => 
{ 
    // 对于长度不足 2 的字符串，前面用 0 填充，比如 '2' 填充成 '02'
    return number.toString().padStart(2, '0'); 
};

/** 
 * 在响应 JSON 中，日期是用数组存储的，比如：
 * "submitDate": [2025, 5, 30, 23, 34, 1]，需要一些操作把它们格式化成一个字符串。
 * 
 * @param {Array} dateArray - 从 JSON 响应中读取的日期数组
 * 
 * @returns {string} 返回合成好的字符串，传入空数组则返回空串
 * 
 * @example
 * formatDate([2025, 5, 30, 23, 34, 1])  // 2025 年 05 月 30 日 23 时 34 分 01 秒
*/
const formatDate = (dateArray) => 
{
    if (!dateArray) 
    {
        console.warn('formatDate(null): Param dataArray is null!');
        return ''; 
    }

    const [year, month, day, hour, minute, second] = dateArray;

    return `
        ${year} 年 ${padStartStr(month)} 月 ${padStartStr(day)} 日
        ${padStartStr(hour)} 时 ${padStartStr(minute)} 分 ${padStartStr(second)} 秒
    `;
};

/**
 * 从URL中提取数字参数值
 * 
 * @param   {string} url    - 完整的 URL 字符串
 * @param   {string} param  - 要提取的参数名
 * @returns {number | null}   提取到的数字值，未找到返回null
 * 
 * @example 
 * extractIDFromURL('http://localhost:8888/api/query/score_record?id=115', 'id')     // 115
 * extractIDFromURL('http://localhost:8888/api/query/paginate_score?page=1', 'page') // 1
 */
const extractIDFromURL = (url, param) => 
{
    try 
    {
        const URLObj   = new URL(url);
        const value    = URLObj.searchParams.get(param);

        if (value === null) { return null; }

        const paramNumber = Number(value);

        return Number.isInteger(paramNumber) ? paramNumber : null;
    }
    catch (error) 
    {
        console.error(
            `URL resolve failed! Cause: ${error.message}.`
        );

        return null;
    }
}

// 导出所有工具函数作为命名空间对象
export default {
    formatDate, extractIDFromURL
};