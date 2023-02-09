def get_logged_in_user():
    user = None if auth.user is None else auth.user.email
    return response.json(dict(user=user))

def get_all_books():
    books = db(db.book).select()
    for book in books:
        reviews = db((db.review.book_id == book.id) & (db.review.rating > 0)).select(db.review.rating) #makes sure book matches its review

        sum = 0
        for review in reviews:
            sum += review.rating

        book.avg_rating = sum / len(reviews) #number of ratings that are returned

    return response.json(dict(books=books))

@auth.requires_login()
def get_your_review():
    review = db((db.review.book_id == request.vars.book_id) & (db.review.email == request.vars.email)).select().first()
    return response.json(dict(review=review))

def get_other_reviews():
    if auth.user is None:
        other_reviews = db(db.review.book_id == request.vars.book_id).select()
    else:
        other_reviews = db( (db.review.book_id == request.vars.book_id) & (db.review.email != auth.user.email) ).select()

    return response.json(dict(other_reviews=other_reviews))

@auth.requires_login()
def update_star():
    db.review.update_or_insert(
        ((db.review.book_id == request.vars.book_id) & (db.review.email == request.vars.email)),
        rating=request.vars.rating,
        book_id=request.vars.book_id
    )
    return "ok"
