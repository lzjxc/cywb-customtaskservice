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
    String content;
    String industry;
    String brand;
    String kind;
    String authLevel;
    String remark;

    @Column(name = "resource_url")
    String resourceUrl;

    @Column(name = "task_url")
    String taskUrl;

    @Column(name = "final_url")
    String finalUrl;

    String merchant;
    String field;
    @Column(name = "sub_category")
    String subCategory;

    String category;
    String source;

    @Column(name = "create_time")
    String createTime;

    @Column(name = "modify_time")
    String modifyTime;

    String status;
    String deadline;
    String available;

}
