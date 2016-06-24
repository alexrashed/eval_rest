package model;

import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-02-17T11:18:28.020+0100")
@StaticMetamodel(ArticleEntity.class)
public class ArticleEntity_ extends BaseEntity_ {
	public static volatile SingularAttribute<ArticleEntity, String> name;
	public static volatile SingularAttribute<ArticleEntity, String> description;
	public static volatile SingularAttribute<ArticleEntity, BigDecimal> price;
}
