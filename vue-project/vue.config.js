const { defineConfig } = require('@vue/cli-service');
const webpack = require('webpack'); // 导入 webpack 模块

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: 7070
  },
  configureWebpack: {
    plugins: [
      new webpack.DefinePlugin({
        __VUE_OPTIONS_API__: JSON.stringify(true),
        __VUE_PROD_HYDRATION_MISMATCH_DETAILS__: JSON.stringify(false) // 根据需要设置为true或false
      })
    ]
  }
});
