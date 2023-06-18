package com.example.terminalweb.entity

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class RootEntity{
    @Id
    private var id: Long = 0;
}