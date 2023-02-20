package com.chenyueworkbench.customtask.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@Entity
@Table(name = "customtasks")
@Data
@Accessors(chain = true)
public class Customtask {
    @Id
    @Column(name = "customtask_id", nullable = false)
    String id;

    String name;

    @Column(name = "contact_name")
    String contactName;

    @Column(name = "leader_name")
    String leaderName;

    @Column(name = "worker_name")
    String workerName;

    String title;
    String industry;

    @Column(name = "insight_input")
    String insightInput;

    @Column(name = "customtask_remark")
    String customtaskRemark;

    @Column(name = "audit_remark")
    String auditRemark;

    String status;
    String merchant;
    String field;

    @Column(name = "sub_category")
    String subCategory;

    String category;
    String source;

    @Column(name = "file_url")
    String fileUrl;

    @Column(name = "create_time")
    String createTime;

    @Column(name = "modify_time")
    String modifyTime;

    @Column(name = "task_deadline")
    String taskDeadline;

    @Column(name = "task_gather_type")
    String taskGatherType;

    @Column(name = "task_gather_url")
    String taskGatherUrl;

    @Column(name = "final_product_url")
    String finalProductUrl;

    @Column(name = "be_template")
    Boolean beTemplate;

    @Column(name = "has_template")
    Boolean hasTemplate;

    String available;

}
