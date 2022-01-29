package com.OBS.entity;

import com.OBS.lab.AcceptedFinishedOrder;
import com.OBS.lab.FinishedOrder;
import com.OBS.lab.InProgressOrder;
import com.OBS.lab.OrderState;
import com.OBS.service.interfaces.SystemFacade;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.json.bind.Jsonb;
import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

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

    @Transient
    public SystemFacade systemService;

    @Transient
    private OrderState state;

    @Transient
    public Jsonb jsonb;

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
