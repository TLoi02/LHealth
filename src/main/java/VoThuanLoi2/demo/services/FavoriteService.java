package VoThuanLoi2.demo.services;

import VoThuanLoi2.demo.entity.Favorite;
import VoThuanLoi2.demo.respository.FavoriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteService {
    @Autowired
    private FavoriteRepository favoriteRepository;

    public void save(Favorite f){
        favoriteRepository.save(f);
    }

    public Boolean isFavorite(Integer idUser, Long idBook){
        Favorite favorite = favoriteRepository.findFavoriteByBookIdAndUserId(idBook, idUser);
        Boolean result = (favorite == null) ? false : true;
        return result;
    }

    public Favorite getFavoriteQuery(Integer idUser, Long idBook){
        return favoriteRepository.findFavoriteByBookIdAndUserId(idBook, idUser);
    }

    public void deleteFavorite(Favorite f){
        favoriteRepository.delete(f);
    }

    public List<Favorite> getFavoriteByUserId(Integer idUser){
        return favoriteRepository.findFavoriteByUserId(idUser);
    }
}
