package VoThuanLoi2.demo.services;

import VoThuanLoi2.demo.entity.Book;
import VoThuanLoi2.demo.entity.Category;
import VoThuanLoi2.demo.respository.IBookRespository;
import VoThuanLoi2.demo.respository.ICategoryRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private ICategoryRespository repo;
    public List<Category> getAllCategories(){
        return repo.findAll();
    }
    public void deleteCategory(Long id){
        repo.deleteById(id);
    }
    public Category getCategoryById(Long id) {
        return repo.findById(id).orElse(null);
    }
    public Category saveCategory(Category c) {
        return repo.save(c);
    }
}
