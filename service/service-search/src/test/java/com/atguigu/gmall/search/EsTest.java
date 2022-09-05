package com.atguigu.gmall.search;

import com.atguigu.gmall.search.bean.Person;
import com.atguigu.gmall.search.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

import java.util.List;
import java.util.Optional;

/**
 * @author tkwrite
 * @create 2022-09-03-20:39
 */
@SpringBootTest
public class EsTest {
    @Autowired
    PersonRepository personRepository;

    @Autowired
    ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Test
    void queryqTest(){
    //    索引： 数据库
    //    索引一条数据，指给es中保存一条数据
    }

    @Test
    void queryTest(){
        // Optional<Person> byId = personRepository.findById(2L);
        // System.out.println(byId.get());

    //    1.查询address在倚天屠龙记的人
    //     List<Person> selectAdd = personRepository.findAllByAddressLike("倚");
    //     for (Person person : selectAdd) {
    //         System.out.println(person);
    //     }
        //    2.查询年龄小于等于20的人
        // List<Person> allByAgeLessThanEqual = personRepository.findAllByAgeLessThanEqual(20);
        // for (Person person : allByAgeLessThanEqual) {
        //     System.out.println(person);
        // }
        //    3.查询年龄大于20且在天龙八部的人
        // List<Person> ageGreaterThanAndAddress = personRepository.findAllByAgeGreaterThanAndAddress(20, "八");
        // for (Person greaterThanAndAddress : ageGreaterThanAndAddress) {
        //     System.out.println(greaterThanAndAddress);
        // }
        //    4.查询年龄大于20且在天龙八部的人 或者id=3的人
        List<Person> allByAgeGreaterThanAndAddressOrId = personRepository.findAllByAgeGreaterThanAndAddressOrId(20, "八", 3L);
        for (Person person : allByAgeGreaterThanAndAddressOrId) {
            System.out.println(person);
        }
    }


    @Test
    void saveTest(){
        Person person = new Person();
        person.setId(0L);
        person.setFirstName("三丰");
        person.setLastName("张");
        person.setAge(120);
        person.setAddress("倚天屠龙记武当山");

        personRepository.save(person);

        Person person1 = new Person();
        person1.setId(1L);
        person1.setFirstName("无忌");
        person1.setLastName("张");
        person1.setAge(20);
        person1.setAddress("倚天屠龙记明教");

        personRepository.save(person1);

        Person person2 = new Person();
        person2.setId(2L);
        person2.setFirstName("芷若");
        person2.setLastName("周");
        person2.setAge(19);
        person2.setAddress("倚天屠龙记峨眉派");

        personRepository.save(person2);

        Person person3 = new Person();
        person3.setId(3L);
        person3.setFirstName("离");
        person3.setLastName("殷");
        person3.setAge(18);
        person3.setAddress("倚天屠龙记明教");

        personRepository.save(person3);

        Person person4 = new Person();
        person4.setId(4L);
        person4.setFirstName("誉");
        person4.setLastName("段");
        person4.setAge(22);
        person4.setAddress("天龙八部大理");

        personRepository.save(person4);

        System.out.println("完成。。。");

    }

}
