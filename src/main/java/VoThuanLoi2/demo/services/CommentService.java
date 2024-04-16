package VoThuanLoi2.demo.services;

import VoThuanLoi2.demo.entity.Comment;
import VoThuanLoi2.demo.respository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    public List<Comment> getCommentByIdBook(Long id){
        return commentRepository.findByBookId(id);
    }

    public void save(Comment c){
        commentRepository.save(c);
    }

    public List<Comment> getCommentByUserId(Integer userId){
        return commentRepository.findByUserId(userId);
    }
}
