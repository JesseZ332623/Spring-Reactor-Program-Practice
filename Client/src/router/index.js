import { createRouter, createWebHistory } from 'vue-router'

import Hello from '../components/Hello.vue';
import NotFound from '../components/NotFound.vue';
import SingleScoreRecordQuery from '../components/SingleScoreRecordQuery.vue'
import PaginationScoreRecordQuery from '../components/PaginationScoreRecordQuery.vue';

/** 配置路由属性。 */
const routes = [
	{
		path: '/record/:id',     			// 对这个路径进行路由
		name: SingleScoreRecordQuery,
		component: SingleScoreRecordQuery,  // 使用 ScoreRecord 组件执行该请求
		props: true,			 			// 需要通过 URL 传递参数
		meta: { title: "Score" }
	},
	{
		path: '/pagination_record/:page',
		name: PaginationScoreRecordQuery,
		component: PaginationScoreRecordQuery,
		props: true,
		meta : { title: "Pagination" }
	},
	{
		path: '/record/',			// 对于未填参数的 URL，重定向回默认值
		redirect: '/record/1'
	},
	{
		path: '/pagination_record/',
		redirect: '/pagination_record/1'
	},
	{
		path: '/',
		name: Hello,
		component: Hello,
		meta: { title: "Welcome!" }
	},
	{
		path: '/:pathMatch(.*)*',
		name: NotFound,
		component: NotFound,
		meta: { title: "404 NOT FOUND!" }
	}
];

const router = createRouter({
	history: createWebHistory(),
	routes
});

router.beforeEach((to, from, next) => 
{
	if (to.meta.title) {
		document.title = to.meta.title;
	}
	next();
});

export default router;