package com.atguigu.gmall.search.repository;

import com.atguigu.gmall.search.bean.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author tkwrite
 * @create 2022-09-03-20:33
 */
@Repository
public interface PersonRepository extends PagingAndSortingRepository<Person,Long> {

//    SpringData:起名工程师
    //    1.查询address在倚天屠龙记的人
    List<Person>findAllByAddressLike(String address);

    //    2.查询年龄小于等于20的人
    List<Person>findAllByAgeLessThanEqual(Integer age);
    //    3.查询年龄大于20且在天龙八部的人
    List<Person>findAllByAgeGreaterThanAndAddress(Integer age, String address);
    //    4.查询年龄大于20且在天龙八部的人 或者id=3的人
    List<Person>findAllByAgeGreaterThanAndAddressOrId(Integer age, String address, Long id);
}
