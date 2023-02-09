def index():
    fields = [
        db.products.item_name,
        db.products.item_stock,
        db.products.item_sold,
        db.products.item_salesprice,
        db.products.item_cost,
        db.products.item_profit,
        db.products.item_star,
        db.products.user_id
    ]

    links = [
        dict(
            header='',
            body= lambda row : 
            A('', _href=URL('default', 'click_star', args=[row.id]), _class='fa fa-star-o')
                if row.item_star is False else
            A('', _href=URL('default', 'click_star', args=[row.id]), _class='fa fa-star')
        )
    ]

    if auth.user is not None:
        links.append(
            dict(
                header='',
                body=lambda row:
                    A('+1', _class='btn', _href=URL('default', 'increase_stock', args=[row.id]))
            )
        )

        links.append(
            dict(
                header='',
                body=lambda row:
                    A('-1', _class='btn', _href=URL('default', 'decrease_stock', args=[row.id]))
                        if row.item_stock > 0 else
                    A('-1')
            )
        )

        links.append(
            dict(
                header='',
                body=lambda row:
                    A('', _href=URL('default', 'edit_product', args=[row.id]), _class='fa fa-pencil-square')
                        if row.user_id == auth.user.id else
                    A('', _class='hidden')
            )
        )

        links.append(
            dict(
                header='',
                body=lambda row:
                    A('', _href=URL('default', 'delete_product', args=[row.id]), _class='fa fa-trash')
                        if row.user_id == auth.user.id else
                    A('', _class='hidden')
            )
        )

    grid = SQLFORM.grid(
        db.products, 
        fields=fields,
        links=links,
        searchable=False, 
        details=False, 
        create=False, 
        deletable=False, 
        editable=False,
        csv=False,
    )
    return dict(grid=grid)

def home():
    rows = db(db.products.item_star == True).select()
    return dict(rows=rows)

def increase_stock():
    product = db(db.products.id == request.args[0]).select().first()
    product.item_stock = product.item_stock + 1
    product.update_record()
    return redirect(URL('default', 'index'))

def decrease_stock():
    product = db(db.products.id == request.args[0]).select().first()
    product.item_stock = product.item_stock - 1
    product.item_sold = product.item_sold + 1
    product.item_profit = product.item_sold * (product.item_salesprice - product.item_cost)
    product.update_record()
    return redirect(URL('default', 'index'))

def click_star():
    product = db(db.products.id == request.args[0]).select().first()
    product.item_star = not product.item_star
    if auth.user is not None:
        product.update_record()
    return redirect(URL('default', 'index'))

def insert_product():
    db.products.id.readable=False
    form = SQLFORM(db.products)
    insert_was_succesful = form.process().accepted
    if insert_was_succesful:
        #db.products.insert(**db.products._filter_fields(form.vars))
        return redirect(URL('default', 'index'))
    elif form.errors:
        response.flash = 'could not insert your product'
    return dict(form=form)

def edit_product():
    db.products.item_profit.readable=False
    db.products.item_sold.readable=False
    db.products.id.readable=False
    product_to_edit = db(db.products.id == request.args[0]).select().first()
    form = SQLFORM(db.products, product_to_edit)
    insert_was_succesful = form.process().accepted
    if insert_was_succesful:
        return redirect(URL('default', 'index'))
    elif form.errors:
        response.flash = 'could not edit your product'
    return dict(form=form)

def delete_product():
    product_to_delete = db(db.products.id == request.args[0]).select().first()
    product_to_delete.delete_record()
    return redirect(URL('default', 'index'))

#def view_movie_details():
#    movie_id = request.args(0)
#    movie = db(db.movies.id == movie_id).select().first()
#    movie.img_src = URL('default', 'download', args=movie.movie_cover)
#    return dict(movie=movie)

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
