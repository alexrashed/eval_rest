package at.ac.tuwien.dsg.bakk.service;

import model.ArticleEntity;

/**
 * Service managing the article entities.
 * 
 * @author Alexander Rashed, 1325897, alexander.rashed@tuwien.ac.at
 */
public class ArticleService extends EntityService<ArticleEntity> {

	public ArticleService() {
		super(ArticleEntity.class);
	}

}
