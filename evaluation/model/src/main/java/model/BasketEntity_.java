package model;

import javax.annotation.Generated;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-05-29T20:35:40.353+0200")
@StaticMetamodel(BasketEntity.class)
public class BasketEntity_ extends BaseEntity_ {
	public static volatile MapAttribute<BasketEntity, ArticleEntity, Long> articlesToAmount;
	public static volatile SingularAttribute<BasketEntity, BillEntity> bill;
	public static volatile SingularAttribute<BasketEntity, String> name;
}
