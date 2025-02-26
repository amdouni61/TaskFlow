package com.example.taskflow.model;

import com.example.taskflow.model.enums.EquipeManagement;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class Equipe {
@Id
@GeneratedValue(strategy = GenerationType.AUTO )
@Column(nullable = false)
    private Integer id;

 private EquipeManagement equipeManagement;
 private  String departement;
 private  String equipe;





}
