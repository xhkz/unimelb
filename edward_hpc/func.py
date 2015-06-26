__author__ = 'Xin Huang'
"""
    Full Name: Xin Huang
    Student ID: 685269
"""
import json


class Conf():
    def __init__(self):
        # default configuration
        self.key = 'it'
        self.path = '/home/projects/pMelb0243/data/Twitter.csv'


conf = Conf()


def get_twitter_json(txt):
    """
        keys in json:
        text
        entities->hashtags[n]->text in if condition
        entities->user_mentions[n]->screen_name
    """
    return json.loads(txt)


def count_key_num(txt):
    return txt.count(conf.key)


def get_mentioned_user(json_obj):
    users = []

    if json_obj['user_mentions']:
        for d in json_obj['user_mentions']:
            users.append(d['screen_name'])

    return users


def get_mentioned_topic(json_obj):
    topics = []

    if json_obj['hashtags']:
        for d in json_obj['hashtags']:
            topics.append(d['text'])

    return topics


def process_line(line):
    try:
        j = get_twitter_json(line[4])
        return count_key_num(j['text']), get_mentioned_user(j['entities']), get_mentioned_topic(j['entities'])
    except ValueError:
        return 0, [], []
