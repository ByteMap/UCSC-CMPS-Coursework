let processBooks = function() {
    let index = 0;
    app.books.map((book) => {
        Vue.set(book, 'index', index++);
        Vue.set(book, 'showReviews', false);
        Vue.set(book, 'otherReviews', []);
        Vue.set(book, 'yourReview', { rating: 0, numStars: 0 });
    });
};

// let processReviews = function(bookIndex) {
//     let book = app.books[bookIndex];
//     let index = 0;
//     book.otherReviews.map((review) => {
//         Vue.set(review, 'index', index++);
//         Vue.set(review, 'numStars', review.rating);
//     });
// };

let getAllBooks = function() {
    $.getJSON(getAllBooksUrl, function(response) {
        app.books = response.books;
        processBooks();
        console.log(app.books)
    });
};

let getLoggedInUser = function(callback) {
    $.getJSON(getLoggedInUserUrl, function(response) {
        app.loggedInUser = response.user;
        callback();
    });
};

let onPageLoad = function() {
    getLoggedInUser(function() {
        getAllBooks();
    });
};

let getYourReview = function(bookIndex) {
    // exit the function if the user is not logged in
    if (app.loggedInUser == undefined) {
        return;
    }

    let book = app.books[bookIndex];

    $.getJSON(getYourReviewUrl, { book_id: book.id, email: app.loggedInUser }, function(response) {
        if (response.review != null) {
            book.yourReview.rating = response.review.rating;
            book.yourReview.numStars = response.review.rating;
        }
    });
};

let getOtherReviews = function(bookIndex) {
    let book = app.books[bookIndex];
    $.getJSON(getOtherReviewsUrl, { book_id: book.id }, function(response) {
        book.otherReviews = response.other_reviews;
        // processReviews(bookIndex);
    });
};

let toggleReviewsSection = function(bookIndex) {
    let book = app.books[bookIndex];
    book.showReviews = !book.showReviews;
};

let hoverStar = function(bookIndex, starNum) {
    let book = app.books[bookIndex];
    book.yourReview.numStars = starNum;
};

let leaveStarRow = function(bookIndex) {
    let book = app.books[bookIndex];
    book.yourReview.numStars = book.yourReview.rating;
};

let clickStar = function(bookIndex, starNum) {
    let book = app.books[bookIndex];
    book.yourReview.rating = starNum;
    $.post(updateStarUrl, {
        book_id: book.id,
        email: app.loggedInUser,
        rating: starNum
    }, function() {

    });

    let sum = 0;
    let numRecords = book.otherReviews.length + 1; // +1 to account for user's rating
    for(let i=0; i<book.otherReviews.length; i++) {
        sum += book.otherReviews[i].rating;
        if (book.otherReviews[i].rating == 0) {
          numRecords--; // if theres a rating with a value of 0. We shouldnt count that
        }
    }
    if (book.yourReview.rating == 0) {
      numRecords --; // if user has rating 0. 
    }

    sum += book.yourReview.rating;
    book.avg_rating = sum / numRecords;
};

let app = new Vue({
    el: "#app",
    delimiters: ['${', '}'],
    unsafeDelimiters: ['!{', '}'],
    data: {
        books: [],
        starIndices: [1, 2, 3, 4, 5],
        loggedInUser: undefined,
    },
    methods: {
        getYourReview: getYourReview,
        toggleReviewsSection: toggleReviewsSection,
        hoverStar: hoverStar,
        leaveStarRow: leaveStarRow,
        clickStar: clickStar
    }
});

onPageLoad();
