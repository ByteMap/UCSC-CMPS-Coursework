import datetime

def get_user_id():
    return None if auth.user is None else auth.user.id

db.define_table('products',
	Field('item_name', requires=IS_NOT_EMPTY()),
	Field('item_description', 'text', requires=IS_NOT_EMPTY()),
	Field('item_stock', 'integer', requires=IS_INT_IN_RANGE(0, 100000000, error_message='negative or too large!')),
	Field('item_sold', 'integer', default=0, writable=False),
	Field('item_salesprice', 'float', requires=IS_FLOAT_IN_RANGE(0, 100000000, dot=".", error_message='negative or too large!')),
	Field('item_cost', 'float', requires=IS_FLOAT_IN_RANGE(0, 100000000, dot=".", error_message='negative or too large!')),
	Field('item_profit', 'float', default=0.0, writable=False),
	Field('item_star', 'boolean', default=False, readable=False, writable=False),
	Field('user_id', 'reference auth_user',default=get_user_id(), readable=False, writable=False)
)

db.define_table('homedisplay',
	Field('item_name'),
	Field('item_salesprice', 'float')
)

#db.define_table('movies',
#    Field('movie_title'),
#    Field('movie_description', 'text'),
#    Field('movie_cover', 'upload'),
#    Field('movie_smile', 'boolean', default=False),
#    Field('movie_release_date', 'datetime', default=datetime.datetime.now()),
#    Field('user_id', 'reference auth_user',default=get_user_id())
#)