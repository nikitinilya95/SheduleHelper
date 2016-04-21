package orm;

import java.io.Serializable;

/**
 * Created by User on 21.04.2016.
 */
public interface GenericDao<T, PK extends Serializable> {

    /** Сохранить объект newInstance в базе данных */
    PK create(T newInstance);

    /** Извлечь объект, предварительно сохраненный в базе данных, используя
     *   указанный id в качестве первичного ключа
     */
    T read(PK id);

    /** Сохранить изменения, сделанные в объекте.  */
    void update(T transientObject);

    /** Удалить объект из базы данных */
    void delete(T persistentObject);
}