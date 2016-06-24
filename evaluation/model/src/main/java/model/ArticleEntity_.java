package model;

import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-05-29T20:48:12.713+0200")
@StaticMetamodel(ArticleEntity.class)
public class ArticleEntity_ extends BaseEntity_ {
	public static volatile SingularAttribute<ArticleEntity, String> name;
	public static volatile SingularAttribute<ArticleEntity, BigDecimal> price;
}
