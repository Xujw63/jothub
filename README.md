通过springboot和mybaitis实现了一个类似github的文件仓库系统，但在具体实现又有所不同。

除注册，登录，更改密码外，前端每次请求都需要设置请求头token来携带后端返回的jwt令牌。

只有管理员才能管理文件和文件夹，普通用户只能下载文件，未激活用户则只能查看系统内容
