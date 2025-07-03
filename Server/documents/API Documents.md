# API Documents

## From TestRequestComponent

- [hello](http://localhost:8888/hello)
- [bye](http://localhost:8888/bye)

## From QueryRequestComponent

### [GET] Query score by ID

- Base URI: <http://localhost:8888/api/query/score_record>
- Parameter
  - id (Number)
- Example: <http://localhost:8888/api/query/score_record?id=114>
- Response

  ```JSON
  {
    "status": "OK",
    "message": "Query score record id = 114 success!",
    "data": {
        "scoreId": 114,
        "userId": 1,
        "userName": "Jesse",
        "submitDate": "2025-05-30 23:34:01",
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
                "href": "http://localhost:8888/api/query/score_record?id=4000"
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
    "time_STAMP": 1751449941350
  }
  ```

### [GET] Query recent score

- Base URI: <http://localhost:8888/api/query/recent_score>
- Parameter
  - count (Number)
- Example: <http://localhost:8888/query/api/recent_score?count=2>
- Response

  ```JSON
  {
    "status": "OK",
    "message": "Query 2 rows.",
    "data": [
        {
            "scoreId": 2784,
            "userId": 1,
            "userName": "Jesse",
            "submitDate": "2025-06-22 22:32:10",
            "correctCount": 29,
            "errorCount": 21,
            "noAnswerCount": 22
        },
        {
            "scoreId": 1193,
            "userId": 1,
            "userName": "Jesse",
            "submitDate": "2025-06-22 21:16:56",
            "correctCount": 15,
            "errorCount": 23,
            "noAnswerCount": 17
        }
    ],
    "time_STAMP": 1751450053135
  }
  ```

### [GET] Query score with pagination

- Base URI: <http://localhost:8888/api/query/paginate_score>
- Parameter
  - page (Number)
- Example: <http://localhost:8888/api/query/paginate_score?page=15>
- Response

  ```JSON
  {
    "status": "OK",
    "message": "Query score record (Page = 15, Size = 8) complete!",
    "data": [
        {
            "scoreId": 3706,
            "userId": 1,
            "userName": "Jesse",
            "submitDate": "2025-06-12 11:35:51",
            "correctCount": 17,
            "errorCount": 2,
            "noAnswerCount": 9
        },
        {
            "scoreId": 3793,
            "userId": 1,
            "userName": "Jesse",
            "submitDate": "2025-06-12 10:24:28",
            "correctCount": 21,
            "errorCount": 29,
            "noAnswerCount": 39
        },
        {
            "scoreId": 1006,
            "userId": 1,
            "userName": "Jesse",
            "submitDate": "2025-06-12 09:33:44",
            "correctCount": 23,
            "errorCount": 11,
            "noAnswerCount": 21
        },
        {
            "scoreId": 2448,
            "userId": 1,
            "userName": "Jesse",
            "submitDate": "2025-06-12 07:46:51",
            "correctCount": 37,
            "errorCount": 5,
            "noAnswerCount": 3
        },
        {
            "scoreId": 600,
            "userId": 1,
            "userName": "Jesse",
            "submitDate": "2025-06-12 07:34:30",
            "correctCount": 5,
            "errorCount": 5,
            "noAnswerCount": 22
        },
        {
            "scoreId": 2782,
            "userId": 1,
            "userName": "Jesse",
            "submitDate": "2025-06-12 05:03:25",
            "correctCount": 10,
            "errorCount": 4,
            "noAnswerCount": 23
        },
        {
            "scoreId": 484,
            "userId": 1,
            "userName": "Jesse",
            "submitDate": "2025-06-12 03:59:54",
            "correctCount": 26,
            "errorCount": 39,
            "noAnswerCount": 29
        },
        {
            "scoreId": 3562,
            "userId": 1,
            "userName": "Jesse",
            "submitDate": "2025-06-12 03:03:22",
            "correctCount": 34,
            "errorCount": 30,
            "noAnswerCount": 36
        }
    ],
    "metadata": {
        "pagination": {
            "size": 8,
            "totalItem": 4000,
            "page": 15
        },
        "_links": {
            "next_page": {
                "method": "GET",
                "href": "http://localhost:8888/api/query/paginate_score?page=16"
            },
            "last_page": {
                "method": "GET",
                "href": "http://localhost:8888/api/query/paginate_score?page=500"
            },
            "first_page": {
                "method": "GET",
                "href": "http://localhost:8888/api/query/paginate_score?page=1"
            },
            "prev_page": {
                "method": "GET",
                "href": "http://localhost:8888/api/query/paginate_score?page=14"
            }
        }
    },
    "time_STAMP": 1751450473074
    }
  ```

### [POST] Add new score

- URI: <http://localhost:8888/api/add_new_score>
- Example of Submit Data

    ```JSON
    {
        "userId": 1,
        "submitDate": "2018-06-30 15:35:46",
        "correctCount": 23,
        "errorCount": 6,
        "noAnswerCount": 3
   }
  ```

- Response

  ```JSON
  {
    "status": "CREATED",
    "message": "Insert new score record id = 4001 complete.",
    "data": {
        "scoreId": 4001,
        "userId": 1,
        "submitDate": "2018-06-30 15:35:46",
        "correctCount": 23,
        "errorCount": 6,
        "noAnswerCount": 3
    },
    "time_STAMP": 1751450584108
  }
  ```
  
### [PUT] Update score by ID

- URI: <http://localhost:8888/api/update_score>
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
    "status": "CREATED",
    "message": "Update score id = 114 complete!",
    "data": {
        "scoreId": 114,
        "userId": 1,
        "submitDate": "2025-07-02 18:04:57",
        "correctCount": 28,
        "errorCount": 6,
        "noAnswerCount": 5
    },
    "time_STAMP": 1751450697696
  }
  ```
  
### [DELETE] Delete score by ID

- Base URI: <http://localhost:8888/api/delete_score>
- Parameter
  - id (Number)
- Example: <http://localhost:8888/api/delete_score?id=114>
- Response

  ```JSON
  {
    "message": "Delete core record id = 114 complete!",
    "statues": "OK",
    "time_STAMP": 1751332659992
  }
  ```

### [DELETE] Truncate score table

- URI: <http://localhost:8888/api/truncate_score>
- Response

  ```JSON
  {
    "message": "Truncate score_record table complete!",
    "statues": "OK",
    "time_STAMP": 1751332721452
  }
  ```
