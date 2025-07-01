import { createRouter, createWebHistory } from 'vue-router'

import SingleScoreRecordQuery from '../components/SingleScoreRecordQuery.vue'
import Hello from '../components/Hello.vue';

/** 配置路由属性。 */
const routes = [
	{
		path: '/record/:id',     // 对这个路径进行路由
		name: SingleScoreRecordQuery,
		component: SingleScoreRecordQuery,  // 使用 ScoreRecord 组件执行该请求
		props: true,			 // 需要通过 URL 传递参数
		meta: {
			title: "Score"
		}
	},
	{
		path: '/record/',
		redirect: '/record/1'
	},
	{
		path: '/record/:id(\\d+)', // 只匹配数字ID
		component: SingleScoreRecordQuery,
		props: true
    },
	{
		path: '/',
		name: Hello,
		component: Hello,
		meta: { title: "Welcome" }
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