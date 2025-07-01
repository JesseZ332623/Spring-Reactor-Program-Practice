# API Documents

## From TestRequestComponent
- [hello](http://localhost:8888/hello)
- [bye](http://localhost:8888/bye)

## From QueryRequestComponent

### [GET] Query score by ID

- Base URI: http://localhost:8888/api/query/score_record
- Parameter
  - id (Number)
- Example: http://localhost:8888/api/query/score_record?id=114
- Response
  ```JSON
  {
    "message": "Query score record id = 114 success!",
    "data": {
        "scoreId": 114,
        "userId": 1,
        "userName": "Jesse",
        "submitDate": [
            2025,
            5,
            30,
            23,
            34,
            1
        ],
        "correctCount": 16,
        "errorCount": 27,
        "noAnswerCount": 8
    },
    "metadata": {
        "_links": {
            "next": {
                "method": "GET",
                "href": "http://localhost:8888/api/query/score_record?id=115"
            },
            "last": {
                "method": "GET",
                "href": "http://localhost:8888/api/query/score_record?id=3996"
            },
            "prev": {
                "method": "GET",
                "href": "http://localhost:8888/api/query/score_record?id=113"
            },
            "first": {
                "method": "GET",
                "href": "http://localhost:8888/api/query/score_record?id=1"
            }
        }
    },
    "statues": "OK",
    "time_STAMP": 1751332024450
  }
  ``` 

### [GET] Query recent score

- Base URI: http://localhost:8888/api/query/recent_score
- Parameter
  - count (Number)
- Example: http://localhost:8888/query/api/recent_score?count=2
- Response
  ```JSON
  {
    "message": "Query 2 rows.",
    "data": [
        {
            "scoreId": 4003,
            "userId": 1,
            "userName": "Jesse",
            "submitDate": [
                2025,
                6,
                30,
                15,
                35,
                47
            ],
            "correctCount": 16,
            "errorCount": 6,
            "noAnswerCount": 3
        },
        {
            "scoreId": 4002,
            "userId": 1,
            "userName": "Jesse",
            "submitDate": [
                2025,
                6,
                30,
                15,
                35,
                47
            ],
            "correctCount": 16,
            "errorCount": 6,
            "noAnswerCount": 3
        }
    ],
    "statues": "OK",
    "time_STAMP": 1751332163315
  }
  ```

### [GET] Query score with pagination

- Base URI: http://localhost:8888/api/query/paginate_score
- Parameter
  - page (Number)
- Example: http://localhost:8888/api/query/paginate_score?page=15
- Response
  ```JSON
  {
    "message": "Query score record (Page = 15, Size = 5) complete!",
    "data": [
        {
            "scoreId": 1948,
            "userId": 1,
            "userName": "Jesse",
            "submitDate": [
                2025,
                6,
                16,
                1,
                23,
                10
            ],
            "correctCount": 20,
            "errorCount": 33,
            "noAnswerCount": 28
        },
        {
            "scoreId": 2759,
            "userId": 1,
            "userName": "Jesse",
            "submitDate": [
                2025,
                6,
                15,
                22,
                16,
                34
            ],
            "correctCount": 12,
            "errorCount": 25,
            "noAnswerCount": 7
        },
        {
            "scoreId": 2273,
            "userId": 1,
            "userName": "Jesse",
            "submitDate": [
                2025,
                6,
                15,
                20,
                22,
                45
            ],
            "correctCount": 13,
            "errorCount": 32,
            "noAnswerCount": 4
        },
        {
            "scoreId": 3711,
            "userId": 1,
            "userName": "Jesse",
            "submitDate": [
                2025,
                6,
                15,
                18,
                42,
                35
            ],
            "correctCount": 38,
            "errorCount": 3,
            "noAnswerCount": 40
        },
        {
            "scoreId": 1285,
            "userId": 1,
            "userName": "Jesse",
            "submitDate": [
                2025,
                6,
                15,
                17,
                40,
                32
            ],
            "correctCount": 29,
            "errorCount": 39,
            "noAnswerCount": 12
        }
    ],
    "metadata": {
        "pagination": {
            "size": 5,
            "totalItem": 3996,
            "page": 15
        },
        "_links": {
            "prev page": {
                "method": "GET",
                "href": "http://localhost:8888/api/query/paginate_score?page=14"
            },
            "next page": {
                "method": "GET",
                "href": "http://localhost:8888/api/query/paginate_score?page=16"
            },
            "last page": {
                "method": "GET",
                "href": "http://localhost:8888/api/query/paginate_score?page=800"
            },
            "first page": {
                "method": "GET",
                "href": "http://localhost:8888/api/query/paginate_score?page=1"
            }
        }
    },
    "statues": "OK",
    "time_STAMP": 1751332289234
  }
  ```

### [POST] Add new score

- URI: http://localhost:8888/api/add_new_score
- Example of Submit Data
    ```JSON
    {
        "userId": 1,
        "submitDate": [2015, 6, 30, 15, 35, 46],
        "correctCount": 23,
        "errorCount": 6,
        "noAnswerCount": 3
   }
  ```
- Response
  ```JSON
  {
    "message": "Insert new score record id = 4005 complete.",
    "data": {
        "scoreId": 4005,
        "userId": 1,
        "submitDate": [
            2015,
            6,
            30,
            15,
            35,
            46
        ],
        "correctCount": 23,
        "errorCount": 6,
        "noAnswerCount": 3
    },
    "statues": "CREATED",
    "time_STAMP": 1751332396830
  }
  ```
  
### [PUT] Update score by ID

- URI: http://localhost:8888/api/update_score
- Example of Submit Data
    ```JSON
    {
        "scoreId": 114,
        "userId": 1,
        "correctCount": 28,
        "errorCount": 6,
        "noAnswerCount": 5
    }
    ```
- Response
  ```JSON
  {
    "message": "Insert new score record id = 114 complete.",
    "data": {
        "scoreId": 114,
        "userId": 1,
        "submitDate": [
            2025,
            7,
            1,
            9,
            16,
            1,
            644329000
        ],
        "correctCount": 28,
        "errorCount": 6,
        "noAnswerCount": 5
    },
    "statues": "CREATED",
    "time_STAMP": 1751332561658
  }
  ```
  
### [DELETE] Delete score by ID

- Base URI: http://localhost:8888/api/delete_score
- Parameter
  - id (Number)
- Example: http://localhost:8888/api/delete_score?id=114
- Response
  ```JSON
  {
    "message": "Delete core record id = 114 complete!",
    "statues": "OK",
    "time_STAMP": 1751332659992
  }
  ```

### [DELETE] Truncate score table

- URI: http://localhost:8888/api/truncate_score
- Response
  ```JSON
  {
    "message": "Truncate score_record table complete!",
    "statues": "OK",
    "time_STAMP": 1751332721452
  }
  ```
