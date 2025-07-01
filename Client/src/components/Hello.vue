<template>
    <div class="github-container">
        <!-- 粒子背景 -->
        <div id="particles-js"></div>

        <!-- 装饰元素 -->
        <div class="floating-element element-1"></div>
        <div class="floating-element element-2"></div>
        <div class="floating-element element-3"></div>

        <div class="container">
            <header class="github-header">
                <h1 class="github-title">Welcome!</h1>
            </header>
        </div>
    </div>
</template>

<script>
export default {
    name: 'GithubWelcome',
    mounted() {
        this.loadParticlesJS();
    },
    methods: {
        loadParticlesJS() {
            const script = document.createElement('script');
            script.src = 'https://cdn.jsdelivr.net/particles.js/2.0.0/particles.min.js';
            script.onload = () => {
                particlesJS('particles-js', {
                    particles: {
                        number: { value: 80, density: { enable: true, value_area: 800 } },
                        color: { value: "#58a6ff" },
                        shape: { type: "circle" },
                        opacity: { value: 0.5, random: true },
                        size: { value: 3, random: true },
                        line_linked: {
                            enable: true,
                            distance: 150,
                            color: "#30363d",
                            opacity: 0.4,
                            width: 1
                        },
                        move: {
                            enable: true,
                            speed: 2,
                            direction: "none",
                            random: true,
                            straight: false,
                            out_mode: "out",
                            bounce: false
                        }
                    },
                    interactivity: {
                        detect_on: "canvas",
                        events: {
                            onhover: { enable: true, mode: "grab" },
                            onclick: { enable: true, mode: "push" },
                            resize: true
                        }
                    },
                    retina_detect: true
                });
            };
            document.head.appendChild(script);
        }
    }
}
</script>

<style scoped>
.github-container {
    min-height: 100%;
    min-width: 100%;
    overflow: hidden;
}

.container {
    margin: 0 auto;
    padding: 3rem;
    position: relative;
    z-index: 10;
}

/* GitHub风格标题 */
.github-header {
    text-align: center;
    padding: 0 auto;
    margin: 0 auto;
    position: relative;
}

.github-title {
    font-size: 6rem;
    font-weight: 800;
    background: linear-gradient(135deg,
            var(--github-blue) 0%,
            var(--github-green) 25%,
            var(--github-purple) 50%,
            var(--github-orange) 75%,
            var(--github-pink) 100%);
    -webkit-background-clip: text;
    background-clip: text;
    color: transparent;
    background-size: 400% 400%;
    animation: gradient-shift 6s ease infinite;
    position: relative;
    display: inline-block;
    text-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
    margin-bottom: 1.5rem;
}

/* 动画效果 */
@keyframes gradient-shift {
    0% {
        background-position: 0% 50%;
    }

    50% {
        background-position: 100% 50%;
    }

    100% {
        background-position: 0% 50%;
    }
}

@keyframes float {
    0% {
        transform: translateY(0px);
    }

    50% {
        transform: translateY(-20px);
    }

    100% {
        transform: translateY(0px);
    }
}

/* 响应式调整 */
@media (max-width: 768px) {
    .github-title {
        font-size: 4rem;
    }

    .github-header {
        padding: 5rem 0 3rem;
    }
}

.element-1 {
    width: 300px;
    height: 300px;
    background: var(--github-blue);
    top: 10%;
    left: 5%;
    animation: float 12s ease-in-out infinite;
}

.element-2 {
    width: 200px;
    height: 200px;
    background: var(--github-purple);
    bottom: 15%;
    right: 10%;
    animation: float 10s ease-in-out infinite;
    animation-delay: 1s;
}

.element-3 {
    width: 150px;
    height: 150px;
    background: var(--github-green);
    top: 40%;
    right: 25%;
    animation: float 8s ease-in-out infinite;
    animation-delay: 2s;
}
</style>