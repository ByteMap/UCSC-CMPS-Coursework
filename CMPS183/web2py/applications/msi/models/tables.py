import datetime

def get_user_id():
    return None if auth.user is None else auth.user.id

db.define_table('movies',
    Field('movie_title'),
    Field('movie_description', 'text'),
    Field('movie_cover', 'upload'),
    Field('movie_smile', 'boolean', default=False),
    Field('movie_release_date', 'datetime', default=datetime.datetime.now()),
    Field('user_id', 'reference auth_user',default=get_user_id())
)