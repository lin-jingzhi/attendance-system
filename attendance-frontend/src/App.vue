<script setup>
import { ref, onMounted, watch } from 'vue'
import Chart from 'chart.js/auto'
import Login from './components/Login.vue'
import axios from 'axios'

const isLoggedIn = ref(false)
const activeTab = ref('dashboard')
let attendanceChart = null
let employeeChart = null

// 员工管理相关数据
const employees = ref([
  {
    id: 1,
    employeeNo: 'EMP001',
    name: '张三',
    department: '技术部',
    position: 'Java开发工程师',
    status: 1
  },
  {
    id: 2,
    employeeNo: 'EMP002',
    name: '李四',
    department: '技术部',
    position: '前端开发工程师',
    status: 1
  }
])

const employeeForm = ref({
  employeeNo: '',
  name: '',
  department: '',
  position: '',
  status: 1
})

const editingEmployee = ref(null)
const showEditModal = ref(false)

// 打卡管理相关数据
const attendanceRecords = ref([])
const attendanceForm = ref({
  employeeId: '',
  clockType: 'clockin'
})

const latestRecords = ref([])

// 统计报表相关数据
const selectedMonth = ref(new Date().toISOString().slice(0, 7)) // 格式: YYYY-MM

onMounted(() => {
  // 检查登录状态
  checkLoginStatus()
  if (isLoggedIn.value) {
    initCharts()
  }
})

watch(activeTab, (newTab) => {
  if (newTab === 'reports' && isLoggedIn.value) {
    setTimeout(() => {
      initCharts()
    }, 100)
  }
})

function checkLoginStatus() {
  // 简单的登录状态检查，实际项目中应该使用localStorage或sessionStorage
  isLoggedIn.value = true // 暂时默认已登录，方便测试
  // 初始化时加载员工数据
  loadEmployees()
  loadAttendanceRecords()
}

// 加载员工数据
function loadEmployees() {
  axios.get('http://localhost:8080/attendance/employee/list')
    .then(response => {
      employees.value = response.data
    })
    .catch(error => {
      console.error('加载员工数据失败:', error)
      // 使用模拟数据
      employees.value = [
        {
          id: 1,
          employeeNo: 'EMP001',
          name: '张三',
          department: '技术部',
          position: 'Java开发工程师',
          status: 1
        },
        {
          id: 2,
          employeeNo: 'EMP002',
          name: '李四',
          department: '技术部',
          position: '前端开发工程师',
          status: 1
        }
      ]
    })
}

// 加载打卡记录
function loadAttendanceRecords() {
  axios.get('http://localhost:8080/attendance/record/list')
    .then(response => {
      // 转换日期时间格式
      attendanceRecords.value = response.data.map(record => {
        if (record.clockInTime) {
          record.clockInTime = new Date(record.clockInTime)
        }
        if (record.clockOutTime) {
          record.clockOutTime = new Date(record.clockOutTime)
        }
        if (record.recordDate) {
          record.recordDate = new Date(record.recordDate)
        }
        // 获取员工姓名
        const employee = employees.value.find(emp => emp.id === record.employeeId)
        record.employeeName = employee ? employee.name : '未知'
        return record
      })
      updateLatestRecords()
    })
    .catch(error => {
      console.error('加载打卡记录失败:', error)
      // 使用模拟数据
      attendanceRecords.value = []
      updateLatestRecords()
    })
}

function handleLogout() {
  isLoggedIn.value = false
}

function initCharts() {
  // 销毁旧图表
  if (attendanceChart) {
    attendanceChart.destroy()
  }
  if (employeeChart) {
    employeeChart.destroy()
  }

  // 解析选择的月份
  const [year, month] = selectedMonth.value.split('-').map(Number)
  const monthName = `${month}月`

  // 模拟考勤数据（实际项目中应该从后端获取）
  const attendanceData = {
    labels: [monthName],
    datasets: [
      {
        label: '正常出勤',
        data: [22],
        backgroundColor: 'rgba(75, 192, 192, 0.6)'
      },
      {
        label: '迟到',
        data: [2],
        backgroundColor: 'rgba(255, 159, 64, 0.6)'
      },
      {
        label: '早退',
        data: [1],
        backgroundColor: 'rgba(255, 99, 132, 0.6)'
      },
      {
        label: '旷工',
        data: [0],
        backgroundColor: 'rgba(153, 102, 255, 0.6)'
      }
    ]
  }

  // 模拟员工部门分布数据
  const departmentData = {
    labels: ['技术部', '人事部', '财务部', '市场部', '销售部'],
    datasets: [{
      data: [12, 4, 3, 5, 8],
      backgroundColor: [
        'rgba(255, 99, 132, 0.6)',
        'rgba(54, 162, 235, 0.6)',
        'rgba(255, 206, 86, 0.6)',
        'rgba(75, 192, 192, 0.6)',
        'rgba(153, 102, 255, 0.6)'
      ]
    }]
  }

  // 初始化考勤统计图表
  const attendanceCtx = document.getElementById('attendanceChart')
  if (attendanceCtx) {
    attendanceChart = new Chart(attendanceCtx, {
      type: 'bar',
      data: attendanceData,
      options: {
        responsive: true,
        plugins: {
          title: {
            display: true,
            text: `${year}年${month}月考勤情况统计`
          }
        }
      }
    })
  }

  // 初始化员工部门分布图表
  const employeeCtx = document.getElementById('employeeChart')
  if (employeeCtx) {
    employeeChart = new Chart(employeeCtx, {
      type: 'pie',
      data: departmentData,
      options: {
        responsive: true,
        plugins: {
          title: {
            display: true,
            text: '员工部门分布'
          }
        }
      }
    })
  }
}

// 月份选择变化处理
function handleMonthChange() {
  initCharts()
}

// 员工管理相关方法
function addEmployee() {
  if (!employeeForm.value.employeeNo || !employeeForm.value.name || !employeeForm.value.department) {
    alert('请填写必填字段')
    return
  }

  axios.post('http://localhost:8080/attendance/employee/add', employeeForm.value)
    .then(response => {
      employees.value.push(response.data)
      // 重置表单
      employeeForm.value = {
        employeeNo: '',
        name: '',
        department: '',
        position: '',
        status: 1
      }
      alert('添加员工成功')
    })
    .catch(error => {
      console.error('添加员工失败:', error)
      alert('添加员工失败')
    })
}

function editEmployee(employee) {
  editingEmployee.value = { ...employee }
  showEditModal.value = true
}

function saveEmployee() {
  // 实际项目中应该调用更新员工的API
  const index = employees.value.findIndex(emp => emp.id === editingEmployee.value.id)
  if (index !== -1) {
    employees.value[index] = { ...editingEmployee.value }
  }
  showEditModal.value = false
  alert('更新员工成功')
}

function deleteEmployee(id) {
  if (confirm('确定要删除这个员工吗？')) {
    // 实际项目中应该调用删除员工的API
    employees.value = employees.value.filter(emp => emp.id !== id)
    alert('删除员工成功')
  }
}

// 打卡管理相关方法
function submitAttendance() {
  if (!attendanceForm.value.employeeId) {
    alert('请输入员工ID')
    return
  }

  const employee = employees.value.find(emp => emp.id === parseInt(attendanceForm.value.employeeId))
  if (!employee) {
    alert('员工不存在')
    return
  }

  const now = new Date()
  const record = {
    employeeId: parseInt(attendanceForm.value.employeeId),
    recordDate: now,
    clockInTime: attendanceForm.value.clockType === 'clockin' ? now : null,
    clockOutTime: attendanceForm.value.clockType === 'clockout' ? now : null
  }

  axios.post('http://localhost:8080/attendance/record/clockin', record)
    .then(response => {
      // 重新加载打卡记录
      loadAttendanceRecords()
      // 重置表单
      attendanceForm.value = {
        employeeId: '',
        clockType: 'clockin'
      }
      alert('打卡成功')
    })
    .catch(error => {
      console.error('打卡失败:', error)
      alert('打卡失败')
    })
}

function updateLatestRecords() {
  // 按时间倒序排序，取最近5条记录
  latestRecords.value = [...attendanceRecords.value]
    .sort((a, b) => {
      const timeA = a.clockInTime || a.clockOutTime
      const timeB = b.clockInTime || b.clockOutTime
      return timeB - timeA
    })
    .slice(0, 5)
}

// 格式化日期时间
function formatDateTime(date) {
  if (!date) return ''
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

// 格式化日期
function formatDate(date) {
  if (!date) return ''
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}
</script>

<template>
  <div v-if="!isLoggedIn">
    <Login />
  </div>
  <div v-else class="min-vh-100 bg-light">
    <!-- 顶部导航栏 -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
      <div class="container-fluid">
        <a class="navbar-brand" href="#">员工考勤管理系统</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
          <ul class="navbar-nav me-auto mb-2 mb-lg-0">
            <li class="nav-item">
              <a 
                class="nav-link" 
                :class="{ active: activeTab === 'dashboard' }"
                href="#" 
                @click.prevent="activeTab = 'dashboard'"
              >
                控制台
              </a>
            </li>
            <li class="nav-item">
              <a 
                class="nav-link" 
                :class="{ active: activeTab === 'employees' }"
                href="#" 
                @click.prevent="activeTab = 'employees'"
              >
                员工管理
              </a>
            </li>
            <li class="nav-item">
              <a 
                class="nav-link" 
                :class="{ active: activeTab === 'attendance' }"
                href="#" 
                @click.prevent="activeTab = 'attendance'"
              >
                打卡管理
              </a>
            </li>
            <li class="nav-item">
              <a 
                class="nav-link" 
                :class="{ active: activeTab === 'reports' }"
                href="#" 
                @click.prevent="activeTab = 'reports'"
              >
                统计报表
              </a>
            </li>
          </ul>
          <div class="d-flex">
            <span class="text-white me-4">管理员</span>
            <button class="btn btn-outline-light" @click="handleLogout">退出</button>
          </div>
        </div>
      </div>
    </nav>

    <!-- 主内容区域 -->
    <div class="container-fluid py-4">
      <!-- 控制台页面 -->
      <div v-if="activeTab === 'dashboard'" class="dashboard">
        <h2 class="mb-4">系统控制台</h2>
        <div class="row">
          <div class="col-md-3">
            <div class="card text-white bg-primary mb-3">
              <div class="card-body">
                <h5 class="card-title">员工总数</h5>
                <p class="card-text display-4">2</p>
              </div>
            </div>
          </div>
          <div class="col-md-3">
            <div class="card text-white bg-success mb-3">
              <div class="card-body">
                <h5 class="card-title">打卡记录</h5>
                <p class="card-text display-4">0</p>
              </div>
            </div>
          </div>
          <div class="col-md-3">
            <div class="card text-white bg-warning mb-3">
              <div class="card-body">
                <h5 class="card-title">今日打卡</h5>
                <p class="card-text display-4">0</p>
              </div>
            </div>
          </div>
          <div class="col-md-3">
            <div class="card text-white bg-danger mb-3">
              <div class="card-body">
                <h5 class="card-title">异常考勤</h5>
                <p class="card-text display-4">0</p>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 最近打卡记录 -->
        <div class="card mt-4">
          <div class="card-header">
            <h5 class="card-title">最近打卡记录</h5>
          </div>
          <div class="card-body">
            <table class="table table-striped">
              <thead>
                <tr>
                  <th>员工ID</th>
                  <th>姓名</th>
                  <th>打卡时间</th>
                  <th>类型</th>
                  <th>状态</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="latestRecords.length === 0">
                  <td colspan="5" class="text-center">暂无打卡记录</td>
                </tr>
                <tr v-for="record in latestRecords" :key="record.id">
                  <td>{{ record.employeeId }}</td>
                  <td>{{ record.employeeName }}</td>
                  <td>{{ formatDateTime(record.clockInTime || record.clockOutTime) }}</td>
                  <td>{{ record.clockInTime ? '上班打卡' : '下班打卡' }}</td>
                  <td><span class="badge bg-success">正常</span></td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <!-- 员工管理页面 -->
      <div v-if="activeTab === 'employees'" class="employees">
        <h2 class="mb-4">员工管理</h2>
        <div class="card mb-4">
          <div class="card-header">
            <h5 class="card-title">添加员工</h5>
          </div>
          <div class="card-body">
            <form @submit.prevent="addEmployee">
              <div class="row mb-3">
                <div class="col-md-4">
                  <label class="form-label">员工编号</label>
                  <input 
                    type="text" 
                    class="form-control" 
                    v-model="employeeForm.employeeNo" 
                    placeholder="请输入员工编号"
                    required
                  >
                </div>
                <div class="col-md-4">
                  <label class="form-label">姓名</label>
                  <input 
                    type="text" 
                    class="form-control" 
                    v-model="employeeForm.name" 
                    placeholder="请输入姓名"
                    required
                  >
                </div>
                <div class="col-md-4">
                  <label class="form-label">部门</label>
                  <input 
                    type="text" 
                    class="form-control" 
                    v-model="employeeForm.department" 
                    placeholder="请输入部门"
                    required
                  >
                </div>
              </div>
              <div class="row mb-3">
                <div class="col-md-4">
                  <label class="form-label">职位</label>
                  <input 
                    type="text" 
                    class="form-control" 
                    v-model="employeeForm.position" 
                    placeholder="请输入职位"
                  >
                </div>
                <div class="col-md-4">
                  <label class="form-label">状态</label>
                  <select class="form-select" v-model="employeeForm.status">
                    <option value="1">在职</option>
                    <option value="0">离职</option>
                  </select>
                </div>
              </div>
              <button type="submit" class="btn btn-primary">添加员工</button>
            </form>
          </div>
        </div>

        <div class="card">
          <div class="card-header">
            <h5 class="card-title">员工列表</h5>
          </div>
          <div class="card-body">
            <table class="table table-striped">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>员工编号</th>
                  <th>姓名</th>
                  <th>部门</th>
                  <th>职位</th>
                  <th>状态</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="employee in employees" :key="employee.id">
                  <td>{{ employee.id }}</td>
                  <td>{{ employee.employeeNo }}</td>
                  <td>{{ employee.name }}</td>
                  <td>{{ employee.department }}</td>
                  <td>{{ employee.position }}</td>
                  <td>
                    <span v-if="employee.status === 1" class="badge bg-success">在职</span>
                    <span v-else class="badge bg-danger">离职</span>
                  </td>
                  <td>
                    <button class="btn btn-sm btn-primary me-2" @click="editEmployee(employee)">编辑</button>
                    <button class="btn btn-sm btn-danger" @click="deleteEmployee(employee.id)">删除</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- 编辑员工模态框 -->
        <div class="modal fade" :class="{ show: showEditModal }" tabindex="-1" role="dialog" style="display: block; background-color: rgba(0,0,0,0.5);">
          <div class="modal-dialog" role="document">
            <div class="modal-content">
              <div class="modal-header">
                <h5 class="modal-title">编辑员工</h5>
                <button type="button" class="btn-close" @click="showEditModal = false"></button>
              </div>
              <div class="modal-body">
                <form @submit.prevent="saveEmployee">
                  <div class="row mb-3">
                    <div class="col-md-6">
                      <label class="form-label">员工编号</label>
                      <input 
                        type="text" 
                        class="form-control" 
                        v-model="editingEmployee.employeeNo" 
                        required
                      >
                    </div>
                    <div class="col-md-6">
                      <label class="form-label">姓名</label>
                      <input 
                        type="text" 
                        class="form-control" 
                        v-model="editingEmployee.name" 
                        required
                      >
                    </div>
                  </div>
                  <div class="row mb-3">
                    <div class="col-md-6">
                      <label class="form-label">部门</label>
                      <input 
                        type="text" 
                        class="form-control" 
                        v-model="editingEmployee.department" 
                        required
                      >
                    </div>
                    <div class="col-md-6">
                      <label class="form-label">职位</label>
                      <input 
                        type="text" 
                        class="form-control" 
                        v-model="editingEmployee.position"
                      >
                    </div>
                  </div>
                  <div class="mb-3">
                    <label class="form-label">状态</label>
                    <select class="form-select" v-model="editingEmployee.status">
                      <option value="1">在职</option>
                      <option value="0">离职</option>
                    </select>
                  </div>
                  <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" @click="showEditModal = false">取消</button>
                    <button type="submit" class="btn btn-primary">保存</button>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 打卡管理页面 -->
      <div v-if="activeTab === 'attendance'" class="attendance">
        <h2 class="mb-4">打卡管理</h2>
        <div class="card mb-4">
          <div class="card-header">
            <h5 class="card-title">员工打卡</h5>
          </div>
          <div class="card-body">
            <form @submit.prevent="submitAttendance">
              <div class="row mb-3">
                <div class="col-md-4">
                  <label class="form-label">员工ID</label>
                  <input 
                    type="number" 
                    class="form-control" 
                    v-model="attendanceForm.employeeId" 
                    placeholder="请输入员工ID"
                    required
                  >
                </div>
                <div class="col-md-4">
                  <label class="form-label">打卡类型</label>
                  <select class="form-select" v-model="attendanceForm.clockType">
                    <option value="clockin">上班打卡</option>
                    <option value="clockout">下班打卡</option>
                  </select>
                </div>
              </div>
              <button type="submit" class="btn btn-primary">提交打卡</button>
            </form>
          </div>
        </div>

        <div class="card">
          <div class="card-header">
            <h5 class="card-title">打卡记录</h5>
          </div>
          <div class="card-body">
            <table class="table table-striped">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>员工ID</th>
                  <th>姓名</th>
                  <th>打卡日期</th>
                  <th>上班时间</th>
                  <th>下班时间</th>
                  <th>状态</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="attendanceRecords.length === 0">
                  <td colspan="7" class="text-center">暂无打卡记录</td>
                </tr>
                <tr v-for="record in attendanceRecords" :key="record.id">
                  <td>{{ record.id }}</td>
                  <td>{{ record.employeeId }}</td>
                  <td>{{ record.employeeName }}</td>
                  <td>{{ formatDate(record.recordDate) }}</td>
                  <td>{{ formatDateTime(record.clockInTime) }}</td>
                  <td>{{ formatDateTime(record.clockOutTime) }}</td>
                  <td><span class="badge bg-success">正常</span></td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <!-- 统计报表页面 -->
      <div v-if="activeTab === 'reports'" class="reports">
        <h2 class="mb-4">统计报表</h2>
        <div class="card">
          <div class="card-header">
            <h5 class="card-title">考勤统计</h5>
          </div>
          <div class="card-body">
            <div class="mb-4">
              <label class="form-label">选择月份</label>
              <input 
                type="month" 
                class="form-control w-25" 
                v-model="selectedMonth"
                @change="handleMonthChange"
              >
            </div>
            <div class="row">
              <div class="col-md-6">
                <canvas id="attendanceChart" width="400" height="300"></canvas>
              </div>
              <div class="col-md-6">
                <canvas id="employeeChart" width="400" height="300"></canvas>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 详细统计数据 -->
        <div class="card mt-4">
          <div class="card-header">
            <h5 class="card-title">详细统计数据</h5>
          </div>
          <div class="card-body">
            <table class="table table-striped">
              <thead>
                <tr>
                  <th>部门</th>
                  <th>正常出勤</th>
                  <th>迟到</th>
                  <th>早退</th>
                  <th>旷工</th>
                  <th>出勤率</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>技术部</td>
                  <td>45</td>
                  <td>3</td>
                  <td>2</td>
                  <td>0</td>
                  <td>95%</td>
                </tr>
                <tr>
                  <td>人事部</td>
                  <td>18</td>
                  <td>1</td>
                  <td>0</td>
                  <td>0</td>
                  <td>95%</td>
                </tr>
                <tr>
                  <td>财务部</td>
                  <td>12</td>
                  <td>0</td>
                  <td>0</td>
                  <td>0</td>
                  <td>100%</td>
                </tr>
                <tr>
                  <td>市场部</td>
                  <td>20</td>
                  <td>2</td>
                  <td>1</td>
                  <td>0</td>
                  <td>93%</td>
                </tr>
                <tr>
                  <td>销售部</td>
                  <td>32</td>
                  <td>4</td>
                  <td>2</td>
                  <td>1</td>
                  <td>88%</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style>
body {
  margin: 0;
  padding: 0;
  font-family: Arial, sans-serif;
}

.nav-link.active {
  font-weight: bold;
}
</style>
