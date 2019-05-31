package br.iesb.android.opiniaodetudo.model.repository

import android.content.Context
import br.iesb.android.opiniaodetudo.model.Review
import br.iesb.android.opiniaodetudo.persistence.ReviewDao
import br.iesb.android.opiniaodetudo.persistence.ReviewDatabase
import java.util.*

class ReviewRepository {
    private val reviewDao: ReviewDao

    constructor(context: Context){
        val reviewDatabase = ReviewDatabase.getInstance(context)
        reviewDao = reviewDatabase.reviewDao()
    }

    fun save(name: String, review: String, photoPath: String?, thumbnailBytes: ByteArray?):Review {
        val review = Review(UUID.randomUUID().toString(), name, review, photoPath, thumbnailBytes)
        reviewDao.save(review)
        return review
    }
    fun listAll(): List<Review> {
        return reviewDao.listAll()
    }

    fun delete(item: Review) {
        return reviewDao.delete(item)
    }

    fun delete(id: String?) {
        return reviewDao.delete(id)
    }

    fun update(id: String, name: String, review: String): Review {
        val review = Review(id, name, review)
        reviewDao.update(review)
        return review
    }

    fun update(review: Review) {
        reviewDao.update(review)
    }
    fun update(review: Review, name: String, reviewText: String?): Review {
        val review1 =
            Review(review.id, name, reviewText, review.photoPath, review.thumbnail, review.latitude, review.longitude)
        reviewDao.update(review1)
        return review1
    }

    fun updateLocation(entity: Review, lat: Double, long: Double) {
        entity.latitude = lat
        entity.longitude = long
        reviewDao.update(entity)
    }
}