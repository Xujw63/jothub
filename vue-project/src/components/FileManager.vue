<template>
  <el-container>
    <el-header class="header">
      <el-row :gutter="20" align="middle" class="header-row">
        <el-col :span="4">
          <h3 class="header-title">文件管理</h3>
        </el-col>
        <el-col :span="20" class="actions">
          <el-input
            v-model="searchQuery"
            placeholder="搜索文件/文件夹"
            class="search-input"
            @input="searchAndFilter"
          />
          <el-select v-model="filterType" placeholder="按类型筛选" class="filter-select" @change="searchAndFilter">
            <el-option label="全部" value=""></el-option>
            <el-option label="文件" value="file"></el-option>
            <el-option label="文件夹" value="folder"></el-option>
          </el-select>
          <el-button type="primary" icon="el-icon-folder-add" @click="createFolder" class="action-button">新建文件夹</el-button>
          <el-upload
            action="http://47.109.192.182:8080/files/upload"
            :headers="uploadHeaders"
            :data="uploadData"
            :show-file-list="false"
            :before-upload="beforeUpload"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
          >
            <el-button type="primary" icon="el-icon-upload" class="action-button">上传文件</el-button>
          </el-upload>
        </el-col>
      </el-row>
    </el-header>

    <el-main>
      <!-- 面包屑导航 -->
      <el-breadcrumb separator="/" class="breadcrumb">
        <el-breadcrumb-item @click="navigateFolder({ folderId: 0 })" class="breadcrumb-link">主文件夹</el-breadcrumb-item>
        <el-breadcrumb-item
          v-for="folder in breadcrumbFolders"
          :key="folder.folderId"
          @click="navigateFolder(folder)"
          class="breadcrumb-link"
        >
          {{ folder.folderName }}
        </el-breadcrumb-item>
      </el-breadcrumb>

      <!-- 文件和文件夹列表 -->
      <el-row :gutter="20" class="file-list">
        <el-col v-for="item in fileAndFolderContents" :key="item.fileId || item.folderId" :span="24" class="file-card">
          <div class="file-row" @click="fileClick(item)">
            <div class="file-icon">
              <el-icon v-if="item.type === 'folder'" class="folder-icon"><folder /></el-icon>
              <el-icon v-else class="file-icon"><document /></el-icon>
            </div>
            <div class="file-details">
              <div class="file-name">{{ item.fileName || item.folderName }}</div>
              <div v-if="item.type === 'file'">类型: {{ item.fileType }}</div>
              <div v-if="item.type === 'file'">大小: {{ formatSize(item.fileSize) }}</div>
              <div>创建时间: {{ formatDate(item.createdAt) }}</div>
            </div>
            <div class="file-actions">
              <el-button
                v-if="item.type !== 'folder'"
                size="small"
                type="success"
                icon="el-icon-download"
                @click.stop="downloadFile(item)"
                class="action-button"
              >
                下载
              </el-button>
              <el-button
                type="danger"
                size="small"
                icon="el-icon-delete"
                @click.stop="item.type === 'folder' ? deleteFolder(item) : deleteFile(item)"
                class="action-button"
              >
                删除
              </el-button>
            </div>
          </div>
        </el-col>
      </el-row>
    </el-main>
  </el-container>
</template>

<script>
import axios from 'axios'; 
import { ElMessage, ElIcon } from 'element-plus';
import { Folder, Document } from '@element-plus/icons-vue';

export default {
  components: {
    ElIcon,
    Folder,
    Document,
  },
  data() {
    return {
      uploadHeaders: { token: localStorage.getItem('token') }, // 上传时的请求头，包含 token
      folderMap: {}, // 用于存储所有文件夹的映射关系
      currentFolder: null,
      originalContents: [], // 存储原始文件和文件夹列表
      fileAndFolderContents: [],
      breadcrumbFolders: [],
      searchQuery: '',
      filterType: '',
      uploadData: { parentFolderId: 0 } // 上传时的额外数据
    };
  },
  created() {
    this.loadFolder({ folderId: 0 }); 
  },
  methods: {
    async loadFolder(item) {
      this.currentFolder = item;
      this.uploadData.parentFolderId = item.folderId; // 动态更新上传文件的目标文件夹 ID
      try {
        const [foldersResponse, filesResponse] = await Promise.all([
          axios.get(`http://47.109.192.182:8080/folders/parent/${item.folderId}`),
          axios.get(`http://47.109.192.182:8080/files`, { params: { parentFolderId: item.folderId } })
        ]);

        if (foldersResponse.data.code !== 1 || filesResponse.data.code !== 1) {
          throw new Error(foldersResponse.data.message || filesResponse.data.message);
        }

        const folders = foldersResponse.data.data;
        const files = filesResponse.data.data;
        folders.forEach(folder => {
          this.folderMap[folder.folderId] = folder;
        });
        this.fileAndFolderContents = [...folders, ...files].sort((a, b) => {
          return a.type === 'folder' && b.type !== 'folder' ? -1 : 1;
        });
        this.originalContents = this.fileAndFolderContents;

        if (item.folderId === 0) {
          this.breadcrumbFolders = [];
        } else {
          this.updateBreadcrumb(item);
        }
      } catch (error) {
        ElMessage.error("加载文件夹失败: " + error.message);
      }
    },
    updateBreadcrumb(item) {
      this.breadcrumbFolders = [item]; // 清空面包屑
      let currentFolderId = item.parentFolderId;

      // 使用 folderMap 快速查找所有父文件夹
      while (currentFolderId !== 0) {
        const folder = this.folderMap[currentFolderId];
        if (folder) {
          this.breadcrumbFolders.unshift(folder); // 添加到面包屑开头
          currentFolderId = folder.parentFolderId; // 更新为父文件夹 ID
        } else {
          break; // 如果找不到文件夹，退出循环
        }
      }
    },
    fileClick(item) {
      if (item.type === 'folder') {
        this.loadFolder(item); 
      }
    },
    async deleteFile(item) {
      try {
        const response = await axios.delete(`http://47.109.192.182:8080/files/${item.fileId}`);
        if (response.data.code === 1) {
          ElMessage.success("删除成功");
          this.loadFolder(this.currentFolder); 
        } else {
          ElMessage.error(response.data.message); 
        }
      } catch (error) {
        ElMessage.error("删除文件失败");
      }
    },
    async deleteFolder(item) {
      try {
        const response = await axios.delete(`http://47.109.192.182:8080/folders/${item.folderId}`);
        if (response.data.code === 1) {
          ElMessage.success("删除成功");
          this.loadFolder(this.currentFolder); 
        } else {
          ElMessage.error(response.data.message); 
        }
      } catch (error) {
        ElMessage.error("删除文件夹失败");
      }
    },
    searchAndFilter() {
      this.fileAndFolderContents = this.originalContents.filter(file =>
        (!this.searchQuery || file.fileName?.includes(this.searchQuery) || file.folderName?.includes(this.searchQuery)) &&
        (!this.filterType || file.type === this.filterType)
      );
    },
    async createFolder() {
      const folderName = prompt("请输入新文件夹名称:"); 
      if (folderName) {
        try {
          const parentFolderId = this.currentFolder.folderId;
          const response = await axios.post(`http://47.109.192.182:8080/folders`, {
            folderName,
            parentFolderId 
          });
          if (response.data.code === 1) {
            ElMessage.success("文件夹创建成功"); 
            this.loadFolder(this.currentFolder);
          } else {
            ElMessage.error(response.data.message);
          }
        } catch (error) {
          ElMessage.error("创建文件夹失败");
        }
      }
    },
    navigateFolder(folderId) {
      this.loadFolder(folderId); 
    },

    beforeUpload(file) {
      const isLt10M = file.size / 1024 / 1024 < 10;
      if (!isLt10M) {
        this.$message.error('上传文件大小不能超过 10MB!');
      }
      return isLt10M;
    },

    handleUploadSuccess(response) {
      try {
        if (response.code === 1) {
          ElMessage.success("文件上传成功");
          this.loadFolder(this.currentFolder); // 上传成功后重新加载当前文件夹内容
        } else {
          ElMessage.error(response.message || "上传失败");
        }
      } catch (error) {
        ElMessage.error("处理响应时发生错误：" + error.message);
      }
    },
    handleUploadError() {
      ElMessage.error("文件上传失败");
    },
    async downloadFile(item) {
      try {
          const response = await axios.get(`http://47.109.192.182:8080/files/download/${item.fileId}`, {
              responseType: 'blob'
          });

          // 检查HTTP状态码
          if (response.status === 200) {
              this.downloadFileBlob(response.data, `${item.fileName}${item.fileType}`);
          }
        } catch (error) {
          if(error.response.status === 403){
            ElMessage.error("用户未激活,无法下载文件");
          }else{
            ElMessage.error("下载失败");
          }
        }
    },
    // 下载文件的辅助函数
    downloadFileBlob(blob, fileName) {
        const url = window.URL.createObjectURL(new Blob([blob]));
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', fileName); // 或者使用 item.fileName + item.fileType
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    },
    formatDate(date) {
      const options = { year: 'numeric', month: '2-digit', day: '2-digit' };
      return new Date(date).toLocaleDateString(undefined, options);
    },
    formatSize(size) {
      const units = ['B', 'KB', 'MB', 'GB'];
      let unitIndex = 0;
      while (size >= 1024 && unitIndex < units.length - 1) {
        size /= 1024;
        unitIndex++;
      }
      return size.toFixed(1) + ' ' + units[unitIndex];
    }
  }
};
</script>

<style scoped>
.header {
  background-color: #f5f5f5;
  padding: 10px 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header-title {
  margin: 0;
  font-size: 20px;
  font-weight: bold;
}

.actions {
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

.search-input,
.filter-select {
  margin-right: 15px;
  width: 180px;
}

.action-button {
  margin-right: 15px;
}

.file-list {
  margin-top: 20px;
}

.file-card {
  background-color: #fff;
  padding: 15px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  transition: box-shadow 0.3s;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.file-card:hover {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.file-row {
  display: flex;
  align-items: center;
  width: 100%;
}

.file-icon {
  font-size: 40px;
  color: #409eff;
}

.file-details {
  flex-grow: 1;
  margin-left: 20px;
}

.file-name {
  font-weight: bold;
  margin-bottom: 5px;
}

.file-actions {
  display: flex;
  gap: 10px;
}

.breadcrumb {
  margin-bottom: 20px;
  font-size: 14px;
}

.breadcrumb-link {
  cursor: pointer;
  color: #409eff;
}

.breadcrumb-link:hover {
  color: #66b1ff;
  text-decoration: underline;
}
</style>
