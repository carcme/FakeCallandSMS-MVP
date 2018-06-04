package me.carc.fakecallandsms_mvp.fragments.cloud.ui.pager;

import java.util.ArrayList;
import java.util.Random;

import me.carc.fakecallandsms_mvp.common.C;
import me.carc.fakecallandsms_mvp.common.TinyDB;
import me.carc.fakecallandsms_mvp.fragments.cloud.data.model.Photo;

/**
 * Manage the favorite list for the cloud images
 * Created by bamptonm on 2/25/17.
 */

public class FavDB {

    private static FavDB favMan;
    private ArrayList<Photo> list;
    private TinyDB tiny;

    private FavDB() {
        this.list = new ArrayList<>(0);
        tiny = TinyDB.getTinyDB();
        loadList();
        favMan = this;
    }

    public static FavDB getFavDB() {
        if(favMan == null)
            return new FavDB();
        return favMan;
    }


    private void loadList() {
        list = tiny.getListPhoto(C.SP_FAVORITE_PHOTO_LIST);
    }

    public void saveList() {
        tiny.putListPhoto(C.SP_FAVORITE_PHOTO_LIST, list);
    }

    public ArrayList<Photo> getList() {
        return list;
    }

    public void add(Photo p) {
        list.add(p);
    }
    public void del(Photo p) {
        list.remove(findById(p.id));
    }
    public void del(long id) {
        list.remove(findById(id));
    }


    public Photo randomFromList() {
        return list.get(new Random().nextInt((list.size()-1) + 1));
    }

    private int findById(long id) {
        for (int i = 0; i < list.size(); i++) {
            Photo photo = list.get(i);
            if(photo.id == id)
                return i;
        }
        return -1;
    }

}
