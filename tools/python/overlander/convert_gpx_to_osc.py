#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from lxml import etree
import lxml.html
from urllib.request import Request, urlopen
import json

# create the outer .osc shell
root = etree.Element("osmChange", version="0.6", generator="overlander")

# declare global variables
id = 9381290681
overlanderCatergories = {
    "Informal Campsite": "freedomcamping",
    "Wild Camping": "freedomcamping",
    "Established Campground": "paidcamping",
    "Fuel Station": "fuel",
    "Propane": "propane",
    "Mechanic and Parts": "mechanic",
    "Water": "water",
    "Sanitation Dump Station": "sanidump",
    "Tourist Attraction": "attraction",
    "Wifi": "wifi",
    "Laundromat": "laundry",
    "Showers": "showers",
    "Parking": "parking",
    "Checkpoint": "police",
    "Warning": "warning",
}

with open('./iOverlander Places - Andorra - 2022-01-13.json') as data_file:
    data = json.load(data_file)
    for place in data:
        # if category is not in list break out of loop
        if place['category'] in overlanderCatergories.keys():
            categoryName = overlanderCatergories[str(place['category'])]
            category = etree.Element("tag", k="overlander", v=str(categoryName))
        else: break

        # create outer node outer element
        timestampArray = str(place['date_verified']).split(' ')
        formattedTimestamp = ''.join([timestampArray[0], 'T', timestampArray[1], 'Z'])
        node = etree.Element("node", id=str(id), version="1", timestamp=formattedTimestamp, lat=str(place['location']['latitude']), lon=str(place['location']['longitude']))

        # add category subtag
        node.append(category)

        # name and description subtags
        name = etree.Element("tag", k="name", v=str(place['name']))
        description = etree.Element("tag", k="overlander:description", v=str(place['description']))
        node.append(name)
        node.append(description)

        # amenities subtags
        if 'Open' in place['amenities']:
            open = etree.Element("tag", k="overlander:open", v=str(place['amenities']['Open']))
            node.append(open)
        
        if 'Electricity' in place['amenities']:
            electricity = etree.Element("tag", k="overlander:electricity", v=str(place['amenities']['Electricity']))
            node.append(electricity)

        if 'Wifi' in place['amenities']:
            wifi = etree.Element("tag", k="overlander:wifi", v=str(place['amenities']['Wifi']))
            node.append(wifi)
        
        if 'Kitchen' in place['amenities']:
            kitchen = etree.Element("tag", k="overlander:kitchen", v=str(place['amenities']['Kitchen']))
            node.append(kitchen)
        
        if 'Restaurant' in place['amenities']:
            restaurant = etree.Element("tag", k="overlander:restaurant", v=str(place['amenities']['Restaurant']))
            node.append(restaurant)
        
        if 'Showers' in place['amenities']:
            showers = etree.Element("tag", k="overlander:showers", v=str(place['amenities']['Showers']))
            node.append(showers)
        
        if 'Water' in place['amenities']:
            water = etree.Element("tag", k="overlander:water", v=str(place['amenities']['Water']))
            node.append(water)
        
        if 'Toilets' in place['amenities']:
            toilets = etree.Element("tag", k="overlander:toilets", v=str(place['amenities']['Toilets']))
            node.append(toilets)
        
        if 'Big Rig Friendly' in place['amenities']:
            bigrigfriendly = etree.Element("tag", k="overlander:bigrigfriendly", v=str(place['amenities']['Big Rig Friendly']))
            node.append(bigrigfriendly)
        
        if 'Tent Friendly' in place['amenities']:
            tentfriendly = etree.Element("tag", k="overlander:tentfriendly", v=str(place['amenities']['Tent Friendly']))
            node.append(tentfriendly)
        
        if 'Pet Friendly' in place['amenities']:
            petfriendly = etree.Element("tag", k="overlander:petfriendly", v=str(place['amenities']['Pet Friendly']))
            node.append(petfriendly)

        if 'Sanitation Dump Station' in place['amenities']:
            sanidump = etree.Element("tag", k="overlander:sanidump", v=str(place['amenities']['Sanitation Dump Station']))
            node.append(sanidump)
        
        #grab the checkins from each place
        placeNameArray = str(place['name']).split()
        placeName = '-'.join(placeNameArray).lower()
        requestUrl = ''.join(['https://www.ioverlander.com/places/', str(place['id']), '-', placeName])
        headers = {'User-Agent': 'Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.3'}
        print(requestUrl)
        req = Request(url=requestUrl, headers=headers)
        content = urlopen(req).read()

        page_html = lxml.html.fromstring(content)

        checkincomments = page_html.xpath('//div[@class="placeCheckin"]//p[1]/text()')
        checkindates = page_html.xpath('//div[@class="placeCheckin"]/div/a[1]/text()')
        checkinauthors = page_html.xpath('//div[@class="placeCheckin"]/div/a[2]/text()')

        for index, checkincomment in enumerate(checkincomments):
            kTag = "overlander:checkin" + str(index + 1) + "comment"
            node.append(etree.Element("tag", k=kTag, v=str(checkincomment)))
        
        for index, checkindate in enumerate(checkindates):
            kTag = "overlander:checkin" + str(index + 1) + "date"
            node.append(etree.Element("tag", k=kTag, v=str(checkindate)))

        for index, checkinauthor in enumerate(checkinauthors):
            kTag = "overlander:checkin" + str(index + 1) + "author"
            node.append(etree.Element("tag", k=kTag, v=str(checkinauthor)))
        
        #source subtag
        source = etree.Element("tag", k="overlander:source", v="iOverlander")

        #append node to root and increment id by 1
        root.append(node)
        id +=1

#write out to file
tree = etree.ElementTree(root)
tree.write("Andorra.osc", xml_declaration=True, encoding='UTF-8', pretty_print=True)