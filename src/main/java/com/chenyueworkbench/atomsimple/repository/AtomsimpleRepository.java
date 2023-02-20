package com.chenyueworkbench.atomsimple.repository;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.chenyueworkbench.atomsimple.model.Atomsimple;

@Repository
public interface AtomsimpleRepository extends CrudRepository<Atomsimple,String>  {
    public Optional<Atomsimple> findById(String atomsimpleId);
    public ArrayList<Atomsimple> findByIdIn(List<String> atomsimpleIdList);
    public ArrayList<Atomsimple> findByContactNameOrLeaderName(String contactName, String leaderName);
    public ArrayList<Atomsimple> findAll();
    public ArrayList<Atomsimple> findByNameContains(String key);
}

