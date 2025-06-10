package com.example.taskflow.repositories;

import com.example.taskflow.model.Team;
import com.example.taskflow.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {


     //Find a team by name.

    Optional<Team> findByName(String name);

    //Check if a team with the given name exists.

    boolean existsByName(String name);

     //Find all teams by team lead.

    List<Team> findByTeamLead(User teamLead);
}
