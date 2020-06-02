(function (WIN) {
    var USER_ROLES = {
        0: '<span class="badge badge-dark">普通用户</span>',
        1: '<span class="badge badge-primary">管理员</span>',
        2: '<span class="badge badge-light">学生</span>',
        3: '<span class="badge badge-info">教师</span>',
        4: '<span class="badge badge-warning">导师</span>',
    };
    var USER_STATE = {
        0: '<span class="badge badge-secondary">未激活</span>',
        1: '<span class="badge badge-success">已激活</span>',
        2: '<span class="badge badge-warning">禁用</span>',
    };
    var PROJECT_STATE = {
        0: '<span class="badge badge-secondary">立项</span>',
        1: '<span class="badge badge-success">结题</span>',
        2: '<span class="badge badge-info">进行中</span>',
    };
    var IN_PROJECT_ROLES = {
        0: '<span class="badge badge-dark">参与者</span>',
        1: '<span class="badge badge-primary">项目负责人</span>',
        2: '<span class="badge badge-warning">项目发起者</span>',
    };
    WIN.Constant = {
        USER_ROLES: USER_ROLES,
        USER_STATE: USER_STATE,
        PROJECT_STATE: PROJECT_STATE,
        IN_PROJECT_ROLES: IN_PROJECT_ROLES,
    }
})(window);
