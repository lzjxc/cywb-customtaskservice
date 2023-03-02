package com.chenyueworkbench.customtask.repository;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.chenyueworkbench.customtask.model.Customtask;

@Repository
public interface CustomtaskRepository extends CrudRepository<Customtask,String>  {
    public Optional<Customtask> findById(String customtaskId);
    public ArrayList<Customtask> findByIdIn(List<String> customtaskIdList);
    public ArrayList<Customtask> findByContactNameOrLeaderName(String contactName, String leaderName);
    public ArrayList<Customtask> findAll();
    public ArrayList<Customtask> findByNameContains(String key);
    public ArrayList<Customtask> findByContactNameOrLeaderNameOrWorkerName(String contactName, String leaderName, String workerName);
}

