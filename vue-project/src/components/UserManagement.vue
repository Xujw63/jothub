<template>
  <el-container style="height: 100vh; overflow: hidden; display: flex; flex-direction: column;">
    <el-header style="text-align: right; font-size: 12px; padding: 10px;">
      <span v-if="!isAuthenticated" class="header-text">未登录</span>
      <span v-else class="header-text">{{ username }}</span>
    </el-header>

    <el-main style="flex: 1; display: flex; justify-content: center; align-items: center;">
      <el-card class="form-card">
        <el-tabs v-model="activeName" type="card">
          <el-tab-pane label="用户注册" name="register">
            <el-form :model="registerForm" status-icon :rules="rules" ref="registerForm" label-width="100px" class="demo-ruleForm">
              <el-form-item label="用户名" prop="username">
                <el-input v-model="registerForm.username" placeholder="请输入用户名"></el-input>
              </el-form-item>
              <el-form-item label="密码" prop="password">
                <el-input type="password" v-model="registerForm.password" autocomplete="off" placeholder="请输入密码"></el-input>
              </el-form-item>
              <el-form-item>
                <div style="display: flex; justify-content: space-between;">
                  <el-button type="primary" @click="submitForm('registerForm')">提交</el-button>
                  <el-button @click="resetForm('registerForm')">重置</el-button>
                </div>
              </el-form-item>
            </el-form>
          </el-tab-pane>
          <el-tab-pane label="用户登录" name="login">
            <el-form :model="loginForm" status-icon :rules="rules" ref="loginForm" label-width="100px" class="demo-ruleForm">
              <el-form-item label="用户名" prop="username">
                <el-input v-model="loginForm.username" placeholder="请输入用户名"></el-input>
              </el-form-item>
              <el-form-item label="密码" prop="password">
                <el-input type="password" v-model="loginForm.password" autocomplete="off" placeholder="请输入密码"></el-input>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleLogin()">登录</el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>
          <el-tab-pane label="修改密码" name="updatePassword">
            <el-form :model="updatePasswordForm" status-icon :rules="rules" ref="updatePasswordForm" label-width="100px" class="demo-ruleForm">
              <el-form-item label="用户名" prop="username">
                <el-input v-model="updatePasswordForm.username" placeholder="请输入用户名"></el-input>
              </el-form-item>
              <el-form-item label="旧密码" prop="oldPassword">
                <el-input type="password" v-model="updatePasswordForm.oldPassword" autocomplete="off" placeholder="请输入旧密码"></el-input>
              </el-form-item>
              <el-form-item label="新密码" prop="newPassword">
                <el-input type="password" v-model="updatePasswordForm.newPassword" autocomplete="off" placeholder="请输入新密码"></el-input>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleUpdatePassword()">修改</el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>
        </el-tabs>
      </el-card>
    </el-main>
  </el-container>
</template>

<script>
import { ElMessage } from 'element-plus';
import axios from 'axios';

export default {
  data() {
    return {
      activeName: 'login',
      isAuthenticated: false,
      username: '',
      registerForm: {
        username: '',
        password: ''
      },
      loginForm: {
        username: '',
        password: ''
      },
      updatePasswordForm: {
        username: '',
        oldPassword: '',
        newPassword: ''
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
        ]
      }
    };
  },
  methods: {
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          axios.post('http://47.109.192.182:8080/register', this.registerForm).then(() => {
            ElMessage({ message: '注册成功', type: 'success' });
            this.activeName = 'login';
          }).catch(() => {
            ElMessage.error('注册失败');
          });
        } else {
          console.log('error submit!!');
          return false;
        }
      });
    },
    resetForm(formName) {
      this.$refs[formName].resetFields();
    },
    handleLogin() {
      axios.post('http://47.109.192.182:8080/login', this.loginForm).then((response) => {
        if (response.data.code === 1) {
          this.isAuthenticated = true;
          this.username = response.data.data.username;
          localStorage.setItem('token', response.data.data);
          ElMessage({ message: '登录成功', type: 'success' });
          this.$router.push('/explorer'); // 跳转到文件管理系统页面
        } else {
          ElMessage.error('登录失败');
        }
      }).catch(() => {
        ElMessage.error('请求失败');
      });
    },
    handleUpdatePassword() {
      const params = {
        username: this.updatePasswordForm.username,
        password: this.updatePasswordForm.oldPassword
      };
      const newPassword = this.updatePasswordForm.newPassword;

      axios.patch('http://47.109.192.182:8080/updatePassword', params, {
        params: {
          password: newPassword
        }
      })
      .then((response) => {
        if (response.data.code === 1) {
          ElMessage({ message: '密码更新成功', type: 'success' });
        } else {
          ElMessage.error('用户名或密码错误');
        }
      })
      .catch(() => {
        ElMessage.error('请求失败');
      });
    },
  }
}
</script>

<style scoped>
body {
  margin: 0;
  font-family: 'Arial', sans-serif;
}

.header-text {
  color: #666;
  font-weight: bold;
}

.form-card {
  width: 400px;
  padding: 20px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  border-radius: 10px;
  background-color: #fff;
  text-align: center;
}

.demo-ruleForm {
  width: 100%;
  margin-top: 20px;
}

.el-input__inner {
  border-radius: 5px;
  border: 1px solid #dcdfe6;
  padding: 10px;
  font-size: 14px;
  color: #333;
}

.el-button {
  border-radius: 5px;
  margin-top: 10px;
  width: 48%; /* 使按钮宽度相等 */
}

.el-button--primary {
  background-color: #409eff;
  border-color: #409eff;
  color: #fff;
}

.el-button--default {
  background-color: #fff;
  border-color: #dcdfe6;
  color: #606266;
}

.el-container {
  height: 100vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  background-image: url('../assets/thumb.jpeg');
  background-size: cover;
  background-position: center;
}
</style>