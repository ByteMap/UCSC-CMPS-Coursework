def index():
    fields = [
        db.movies.movie_title,
        db.movies.movie_description,
        db.movies.movie_cover,
        db.movies.user_id,
        db.movies.movie_smile
    ]
    links = [
        dict(
            header='Movie Cover',
            body= lambda row : IMG(_src=URL('default', 'download', args=row.movie_cover), _width='50')
        ),
        dict(
            header='',
            body= lambda row : A('', _href=URL('default', 'view_movie_details', args=[row.id]), _class='fa fa-eye')
        )
    ]

    if auth.user is not None:
        links.append(
            dict(
                header='',
                body= lambda row : 
                    A('', _href=URL('default', 'click_smile', args=[row.id]), _class='fa fa-frown-o')
                        if row.movie_smile is False else
                    A('', _href=URL('default', 'click_smile', args=[row.id]), _class='fa fa-smile-o')
            )
        )
        links.append(
            dict(
                header='',
                body= lambda row : 
                    A('', _href=URL('default', 'edit_movie', args=[row.id]), _class='fa fa-pencil-square')
                        if row.user_id == auth.user.id else
                    A('', _class='hidden')
            )
        )

    grid = SQLFORM.grid(
        db.movies, 
        fields=fields,
        links=links,
        searchable=False, 
        details=False, 
        create=False, 
        deletable=False, 
        editable=False
    )
    return dict(grid=grid)

def click_smile():
    movie = db(db.movies.id == request.args[0]).select().first()
    movie.movie_smile = not movie.movie_smile
    movie.update_record()
    return redirect(URL('default', 'index'))

def insert_movie():
    form = SQLFORM(db.movies)
    insert_was_succesful = form.process().accepted
    if insert_was_succesful:
        return redirect(URL('default', 'index'))
    elif form.errors:
        response.flash = 'could not insert your movie'

    return dict(form=form)

def edit_movie():
    movie_to_edit = db(db.movies.id == request.args[0]).select().first()
    form = SQLFORM(db.movies, movie_to_edit)
    insert_was_succesful = form.process().accepted
    if insert_was_succesful:
        return redirect(URL('default', 'index'))
    elif form.errors:
        response.flash = 'could not edit your movie'

    return dict(form=form)

def view_movie_details():
    movie_id = request.args(0)
    movie = db(db.movies.id == movie_id).select().first()
    movie.img_src = URL('default', 'download', args=movie.movie_cover)
    return dict(movie=movie)

# ---- API (example) -----
@auth.requires_login()
def api_get_user_email():
    if not request.env.request_method == 'GET': raise HTTP(403)
    return response.json({'status':'success', 'email':auth.user.email})

# ---- Smart Grid (example) -----
@auth.requires_membership('admin') # can only be accessed by members of admin groupd
def grid():
    response.view = 'generic.html' # use a generic view
    tablename = request.args(0)
    if not tablename in db.tables: raise HTTP(403)
    grid = SQLFORM.smartgrid(db[tablename], args=[tablename], deletable=False, editable=False)
    return dict(grid=grid)

# ---- Embedded wiki (example) ----
def wiki():
    auth.wikimenu() # add the wiki to the menu
    return auth.wiki() 

# ---- Action for login/register/etc (required for auth) -----
def user():
    """
    exposes:
    http://..../[app]/default/user/login
    http://..../[app]/default/user/logout
    http://..../[app]/default/user/register
    http://..../[app]/default/user/profile
    http://..../[app]/default/user/retrieve_password
    http://..../[app]/default/user/change_password
    http://..../[app]/default/user/bulk_register
    use @auth.requires_login()
        @auth.requires_membership('group name')
        @auth.requires_permission('read','table name',record_id)
    to decorate functions that need access control
    also notice there is http://..../[app]/appadmin/manage/auth to allow administrator to manage users
    """
    return dict(form=auth())

# ---- action to server uploaded static content (required) ---
@cache.action()
def download():
    """
    allows downloading of uploaded files
    http://..../[app]/default/download/[filename]
    """
    return response.download(request, db)
