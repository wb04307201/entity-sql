<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>${data.tableName}</title>
    <link rel="stylesheet" type="text/css" href="${contextPath}/entity/web/static/layui/2.11.5/css/layui.css"/>
    <script type="text/javascript" src="${contextPath}/entity/web/static/layui/2.11.5/layui.js"></script>
    <script type="text/javascript" src="${contextPath}/entity/web/static/echarts/5.5.0/echarts.min.js"></script>
    <style>
        body {
            padding: 10px 20px 10px 20px;
        }
    </style>
</head>
<body>
<!-- 查询区域 -->
<form class="layui-form layui-row layui-col-space16">
    <#list data.columns as item>
        <#if item.getSearch().searchable>
            <div class="layui-col-md4">
                <div class="layui-form-item">
                    <label class="layui-form-label">${item.label}</label>
                    <div class="layui-input-block">
                        <#if item.getEdit().type?? && item.getEdit().type == 'SELECT'>
                            <select name="${item.field}">
                                <option value="" selected>全部</option>
                                <#if item.items?size gt 0>
                                    <#list item.items as option>
                                        <option value="${option.value}">${option.label}</option>
                                    </#list>
                                </#if>
                            </select>
                        <#elseif item.getEdit().type?? && item.getEdit().type == 'NUMBER'>
                            <input type="text" name="${item.field}" placeholder="${item.getEdit().placeholder}"
                                   class="layui-input"
                                   lay-affix="number">
                        <#elseif item.getEdit().type?? && item.getEdit().type == 'DATE'>
                            <input type="text" name="${item.field}" class="layui-input"
                                   id="search-${item.field}"
                                   placeholder="${item.getEdit().placeholder}"
                                   lay-affix="clear">
                        <#elseif item.getEdit().type?? && item.getEdit().type == 'CHECKBOX'>
                            <input type="checkbox" name="${item.field}" lay-skin="switch" lay-filter="switchTest"
                                   title="${item.getEdit().items[0].label}|全部"
                                   value="${item.getEdit().items[0].value}">
                        <#else>
                            <input type="text" name="${item.field}" placeholder="${item.getEdit().placeholder}"
                                   class="layui-input"
                                   lay-affix="clear">
                        </#if>
                    </div>
                </div>
            </div>
        </#if>
    </#list>
    <div class="layui-btn-container layui-col-xs12" style="text-align: right;">
        <button class="layui-btn" lay-submit lay-filter="table-search">查询</button>
        <button type="reset" class="layui-btn layui-btn-primary">重置</button>
    </div>
</form>
<!-- 原始容器 -->
<table class="layui-hide" id="table"></table>
<!-- 工具栏模板 -->
<script type="text/html" id="table-toolbar">
    <div class="layui-btn-container">
        <button class="layui-btn layui-btn-sm" lay-event="add">新建</button>
        <button type="button" class="layui-btn layui-btn-sm layui-bg-red" lay-event="del">删除</button>
    </div>
</script>
<!-- 操作列 -->
<script type="text/html" id="table-templet-operator">
    <div class="layui-clear-space">
        <a class="layui-btn layui-btn-xs" lay-event="delete">删除</a>
        <a class="layui-btn layui-btn-xs" lay-event="edit">编辑</a>
    </div>
</script>
<!-- 弹出层 -->
<div id="edit-layer-wrapper" style="display: none;">
    <div class="layui-form" lay-filter="filter-edit-layer" style="margin: 16px;">
        <#list data.columns as item>
            <#if item.getEdit().editable>
                <div class="layui-col-md4"<#if item.isKey> style="display: none"</#if>>
                    <div class="layui-form-item">
                        <label class="layui-form-label">${item.label}</label>
                        <div class="layui-input-block">
                            <#if item.getEdit().type?? && item.getEdit().type == 'SELECT'>
                                <select name="${item.field}"<#if item.getEdit().required?? && item.getEdit().required> lay-verify="required"</#if>>
                                    <#if item.items?size gt 0>
                                        <#list item.items as option>
                                            <option value="${option.value}">${option.label}</option>
                                        </#list>
                                    </#if>
                                </select>
                            <#elseif item.getEdit().type?? && item.getEdit().type == 'NUMBER'>
                                <input type="text" name="${item.field}"
                                       placeholder="${item.getEdit().getPlaceholder()}"
                                       class="layui-input"
                                       lay-affix="number"<#if item.getEdit().required?? && item.getEdit().required> lay-verify="required"</#if>
                                       lay-precision="${item.scale}">
                            <#elseif item.getEdit().type?? && item.getEdit().type == 'DATE'>
                                <input type="text" name="${item.field}" class="layui-input"
                                       id="form-${item.field}"
                                       placeholder="${item.getEdit().getPlaceholder()}"
                                       lay-affix="clear"<#if item.getEdit().required?? && item.getEdit().required> lay-verify="required"</#if>>
                            <#elseif item.getEdit().type?? && item.getEdit().type == 'CHECKBOX'>
                                <input type="checkbox" name="${item.field}" lay-skin="switch"
                                       lay-filter="switchTest"
                                       title="${item.items[0].label}|${item.items[1].label}"<#if item.getEdit().required?? && item.getEdit().required> lay-verify="required"</#if>
                                       value="${item.items[0].value}">
                            <#else>
                                <input type="text" name="${item.field}"
                                       placeholder="${item.getEdit().getPlaceholder()}"
                                       class="layui-input"
                                       lay-affix="clear"<#if item.getEdit().required?? && item.getEdit().required> lay-verify="required"</#if>>
                            </#if>
                        </div>
                    </div>
                </div>
            </#if>
        </#list>
    </div>
</div>
<div id="echarts-layer-wrapper" style="display: none;">
    <div style="width: 480px;height: 600px;float: left;">
        <form class="layui-form layui-row layui-col-space16" style="margin-top: 20px;">
            <div class="layui-col-md12">
                <div class="layui-form-item">
                    <label class="layui-form-label">x轴</label>
                    <div class="layui-input-block">
                        <select name="xLabelValue">
                            <option value="">请选择</option>
                            <#list data.columns as item>
                                <#if item.getView().viewable && item.getType() != 'NUMBER'>
                                    <option value="${item.field}">${item.label}</option>
                                </#if>
                            </#list>
                        </select>
                    </div>
                </div>
            </div>
            <div class="layui-col-md12">
                <div class="layui-form-item">
                    <label class="layui-form-label">y轴</label>
                    <div class="layui-input-block">
                        <select name="yLabelValue">
                            <option value="">请选择</option>
                            <#list data.columns as item>
                                <#if item.getView().viewable && item.getType() == 'NUMBER'>
                                    <option value="${item.field}">${item.label}</option>
                                </#if>
                            </#list>
                        </select>
                    </div>
                </div>
            </div>
            <div class="layui-btn-container layui-col-xs12" style="text-align: right;">
                <button class="layui-btn" lay-submit lay-filter="draw-chart">绘制</button>
            </div>
        </form>
    </div>
    <div id="echarts" style="width: calc(100% - 500px);height: 600px;float: right;"></div>
</div>
<script>
    layui.use(['table', 'form', 'util'], function () {
        let table = layui.table, form = layui.form, layer = layui.layer, $ = layui.$, laydate = layui.laydate,
            transfer = layui.transfer, util = layui.util;

        let tableData = [];
        let colNames = [
            <#list data.columns as item>
            {value: '${item.field}', label: '${item.label}'},
            </#list>
        ];

        <#list data.columns as item>
        <#if item.getSearch().searchable && item.getEdit().type?? && item.getEdit().type == 'DATE'>
        laydate.render({
            elem: '#search-${item.field}'
        });
        </#if>
        </#list>

        <#list data.columns as item>
        <#if item.getEdit().editable && item.getEdit().type?? && item.getEdit().type == 'DATE'>
        laydate.render({
            elem: '#form-${item.field}'
        });
        </#if>
        </#list>

        // 搜索提交
        form.on('submit(table-search)', function (data) {
            var field = data.field; // 获得表单字段
            <#list data.columns as item>
            <#if item.getEdit().type?? && item.getEdit().type == 'CHECKBOX'>
            if (field.${item.field} == '${item.items[0].value}') field.${item.field} = '${item.items[0].value}'
            else field.${item.field} = ''
            </#if>
            </#list>
            // 执行搜索重载
            Object.keys(field).forEach(key => {
                if (field[key] === "") {
                    delete field[key]; // 删除空字符串属性
                }
            });
            table.reload('table', {
                where: {wheres: field} // 搜索的字段
            });
            return false; // 阻止默认 form 跳转
        });

        table.render({
            elem: '#table',
            cols: [[ //标题栏
                {type: 'checkbox', fixed: 'left'},
                {type: 'numbers', fixed: 'left'},
                <#list data.columns as item>
                <#if item.getView().viewable>
                {
                    field: '${item.field}',
                    title: '${item.label}',
                    <#if item.getView().width??>width: ${item.getView().width}, </#if>
                    <#if item.isKey>hide: true, </#if>
                    <#if item.getView().sortable>sort: true, </#if>
                    <#if item.items?size gt 0>templet: function (d) {
                        <#list item.items as option>
                        if (d.${item.field} === '${option.value}')
                            return '${option.label}';
                        </#list>
                        return ''
                    }, </#if>
                    <#if  item.getType() == 'DATE'>templet: function (d) {
                        return d.${item.field} ? (d.${item.field}.length > 10 ? d.${item.field}.slice(0, 10) : d.${item.field}) : ''
                    }, </#if>
                },
                </#if>
                </#list>
                {field: 'operator', title: '操作', width: 110, fixed: 'right', templet: '#table-templet-operator'},
            ]],
            where: {wheres: {}},
            url: '${contextPath}/entity/select/${id}',
            method: 'post',
            contentType: 'application/json',
            parseData: function (res) { // res 即为原始返回的数据
                tableData = res.data;
                return {
                    "code": res.code === 200 ? 0 : res.code, // 解析接口状态
                    "msg": res.message, // 解析提示文本
                    "count": res.data.length, // 解析数据长度
                    "data": res.data // 解析数据列表
                };
            },
            // height: 'full',
            toolbar: '#table-toolbar',
            defaultToolbar: ['filter', 'exports', 'print', {
                title: '图表',
                layEvent: 'LAYTABLE_CHART',
                icon: 'layui-icon-chart'
            }],
        });

        // 头部工具栏事件
        table.on('toolbar(table)', function (obj) {
            let options = obj.config; // 获取当前表格属性配置项
            let checkStatus = table.checkStatus(options.id); // 获取选中行相关数据

            // 根据不同的事件名进行相应的操作
            switch (obj.event) { // 对应模板元素中的 lay-event 属性值
                case 'add':
                    form.val('filter-edit-layer', {
                        <#list data.columns as item>
                        "${item.field}": "",
                        </#list>
                    });
                    openRow();
                    break;
                case 'del':
                    if (checkStatus.data.length === 0)
                        return layer.msg('请选择至少一行');
                    delRow(checkStatus.data);
                    break;
                case 'LAYTABLE_CHART':
                    openChart();
                    break;
            }
        });

        // 操作列事件
        table.on('tool(table)', function (obj) {
            let data = obj.data; // 获得当前行数据
            switch (obj.event) {
                case 'edit':
                    editRow(data)
                    break;
                case 'delete':
                    delRow([data])
                    break;
            }
        })

        function openRow() {
            layer.open({
                type: 1, // page 层类型，其他类型详见「基础属性」
                title: '新增',
                offset: 'r',
                anim: 'slideLeft', // 从右往左
                area: ['80%', '100%'],
                content: $('#edit-layer-wrapper'),
                btn: ['保存', '取消'],
                btn1: function (index, layero, that) {
                    form.submit('filter-edit-layer', function (data) {
                        let field = data.field; // 获取表单全部字段值
                        <#list data.columns as item>
                        <#if item.getEdit().type?? && item.getEdit().type == 'CHECKBOX'>
                        if (field.${item.field} != '${item.items[0].value}') field.${item.field} = '${item.items[1].value}'
                        </#if>
                        </#list>
                        Object.keys(field).forEach(key => {
                            if (field[key] === "") {
                                delete field[key]; // 删除空字符串属性
                            }
                        });
                        fetch("${contextPath}/entity/save/${id}", {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json'
                            },
                            body: JSON.stringify(field)
                        })
                            .then(response => response.json())
                            .then(res => {
                                if (res.code === 200) {
                                    form.val('filter-edit-layer', {
                                        <#list data.columns as item>
                                        "${item.field}": "",
                                        </#list>
                                    });
                                    table.reloadData('table', {});
                                    layer.close(index);
                                    layer.msg(res.message);
                                } else {
                                    layer.msg(res.message);
                                }
                            })
                            .catch(err => layer.msg(err))
                    });
                },
                btn2: function (index, layero, that) {
                },
            });
        }

        function delRow(rows) {
            layer.confirm('确定要删除么？', {icon: 3}, function (index, layero, that) {
                fetch("${contextPath}/entity/deleteByIds/${id}", {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({data: rows})
                })
                    .then(response => response.json())
                    .then(res => {
                        table.reloadData('table', {});
                        layer.close(index);
                        layer.msg(res.message);
                    })
                    .catch(err => {
                        layer.msg(err)
                        layer.close(index);
                    })
            }, function (index, layero, that) {
            });
        }

        function editRow(data) {
            form.val('filter-edit-layer', {
                <#list data.columns as item>
                "${item.field}": "",
                </#list>
            });
            fetch("${contextPath}/entity/getById/${id}", {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            })
                .then(response => response.json())
                .then(res => {
                    if (res.code === 200) {
                        let data = res.data;
                        <#list data.columns as item>
                        <#if item.getEdit().editable && item.getType() == 'DATE'>
                        data.${item.field} = data.${item.field} ? (data.${item.field}.length > 10 ? data.${item.field}.slice(0, 10) : data.${item.field}) : '';
                        </#if>
                        <#if item.getEdit().type?? && item.getEdit().type == 'CHECKBOX'>
                        data.${item.field} = data.${item.field} === '${item.items[0].value}'
                        </#if>
                        </#list>
                        form.val('filter-edit-layer', data);
                        openRow();
                    } else {
                        layer.msg(res.message);
                    }
                })
                .catch(err => layer.msg(err))
        }

        function openChart() {
            layer.open({
                type: 1, // page 层类型
                title: '图表',
                area: ['80%', 'auto'],
                content: $('#echarts-layer-wrapper'),
                btn: ['关闭'],
                btn1: function (index, layero, that) {
                    layer.close(index);
                },
            });
        }

        form.on('submit(draw-chart)', function (data) {
            if (data.field.xLabelValue != null && data.field.yLabelValue != null) {
                showChart(data.field.xLabelValue, data.field.yLabelValue)
            }
            return false;
        })

        function showChart(xLabelValue, yLabelValue) {
            let yTitleName = colNames.filter(item => item.value === yLabelValue)[0].label
            let groupSum = tableData.reduce((acc, item) => {
                acc[item[xLabelValue]] = (acc[item[xLabelValue]] || 0) + item[yLabelValue];
                return acc;
            }, {});
            let xAxisData = Object.keys(groupSum)
            let serieData = Object.values(groupSum)


            // 基于准备好的dom，初始化echarts实例
            var myChart = echarts.init(document.getElementById('echarts'));

            // 指定图表的配置项和数据
            var option = {
                toolbox: {
                    show: true,
                    feature: {
                        dataView: { readOnly: false },
                        magicType: { type: ['line', 'bar'] },
                        saveAsImage: {}
                    }
                },
                xAxis: {
                    data: xAxisData
                },
                yAxis: {
                    type: 'value'
                },
                series: [
                    {
                        name: yTitleName,
                        type: 'bar',
                        data: serieData
                    }
                ]
            };

            // 使用刚指定的配置项和数据显示图表。
            myChart.setOption(option);
        }
    })
</script>
</body>
</html>