package com.OBS.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@Setter
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Order {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(
            nullable = false,
            updatable = false
    )
    private Long order_Id;

    private String orderType;
    private String decision;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createDate;
    private Boolean isActive;

    @Transient
    private String waitingTime;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private String requestBody;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ordering_employee_id")
    private Employee orderingEmployee;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    public Order() {

    }

    public String getWaitingTime() {
        LocalDateTime now = LocalDateTime.now();

        Duration duration = Duration.between( this.createDate,now);

        return String.format("%dD.%dH.%dM.%dS",
                (int) duration.toDays() % 31,
                (int) duration.toHours() % 24,
                ((int) duration.toMinutes() % 24) % 60,
                (((int) duration.getSeconds()  % 24) % 60) % 60
        );
    }

}
