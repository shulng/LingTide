<template>
  <footer class="app-footer">
    <div class="footer-content">
      <p>© 2026 LingTide Video Platform. All rights reserved.</p>
      <p class="links">
        <a href="#">关于我们</a>
        <span>|</span>
        <a href="#">用户协议</a>
        <span>|</span>
        <a href="#">隐私政策</a>
      </p>
    </div>
  </footer>
</template>

<style lang="scss" scoped>
.app-footer {
  background: #1a1a2e;
  color: #999;
  text-align: center;
  padding: 20px;
  margin-top: auto;
}

.footer-content {
  max-width: 1400px;
  margin: 0 auto;
}

.links {
  margin-top: 8px;
}

.links a {
  color: #999;
  text-decoration: none;
  margin: 0 8px;
  transition: color 0.2s;
}

.links a:hover {
  color: #fff;
}
</style>
