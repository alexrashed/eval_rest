package model;

import javax.annotation.Generated;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-02-17T12:10:43.401+0100")
@StaticMetamodel(BasketEntity.class)
public class BasketEntity_ extends BaseEntity_ {
	public static volatile MapAttribute<BasketEntity, ArticleEntity, Long> articlesToAmount;
	public static volatile SingularAttribute<BasketEntity, BillEntity> bill;
}
