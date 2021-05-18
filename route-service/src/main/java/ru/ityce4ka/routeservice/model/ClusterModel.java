package ru.ityce4ka.routeservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "clusters")
public class ClusterModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(name = "clusterStoreMaping",
            joinColumns = {@JoinColumn(name = "clusterId")},
            inverseJoinColumns = {@JoinColumn(name = "storeId")})
    private List<StoreModel> stores = new ArrayList<>();

    public ClusterModel(List<StoreModel> stores){
        this.stores = stores;
    }

}
