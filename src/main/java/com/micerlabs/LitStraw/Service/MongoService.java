package com.micerlabs.LitStraw.Service;

import com.micerlabs.LitStraw.Dao.MongoDao;
import com.micerlabs.LitStraw.Domain.Literature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MongoService {

    @Autowired
    private MongoDao mongodao;

    // ADD
    /**
     * 保存一个Literature
     */
    public void saveLiterature(Literature literature) {
        mongodao.save(literature);
    }

    // FIND
    /**
     * 查询一个Literature
     */
    public Literature findLiteratureBySample(Example<Literature> literatureExample) {
        return mongodao.findOne(literatureExample).get();
    }

    /**
     * 根据Title找到Literature
     *
     * @param title
     * @return
     */
    public Literature findLiteratureByTitle(String title) {
        return mongodao.findByTitle(title);
    }


    /**
     * 根据title查询LiteratureId
     */
    public String getLiteratureIdByTitle(String title) {
        Literature literatureByTitle = findLiteratureByTitle(title);
        return literatureByTitle.getId();
    }

    public Literature findLiteratureById(String id){
        return mongodao.findById(id).get();
    }

    /**
     * 查询所有Literature
     */
    public List<Literature> findAllLiterature() {
        return mongodao.findAll();
    }

    // DELETE

    /**
     * 删除一个Literature
     */
    public void deleteLiteratureByTitle(String title) {
        mongodao.deleteById(getLiteratureIdByTitle(title));
    }

    // UPDATE
    /**
     * 更新Literature 业务简单，暂时用不到Update
     */

}
