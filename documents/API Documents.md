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

### [GET] Query recent score

- Base URI: http://localhost:8888/api/query/recent_score
- Parameter
  - count (Number)
- Example: http://localhost:8888/query/api/recent_score?count=15

### [GET] Query score with pagination

- Base URI: http://localhost:8888/api/query/paginate_score
- Parameter
  - page (Number)
- Example: http://localhost:8888/api/query/paginate_score?page=5

### [POST] Add new score

- URI: http://localhost:8888/api/add_new_score
- Example of Submit Data
    ```JSON
    {
        "userId": 1,
        "submitDate": [2025, 6, 30, 15, 35, 46, 518413101],
        "correctCount": 16,
        "errorCount": 6,
        "noAnswerCount": 3
    }
    ```
  
### [PUT] Update score by ID

- URI: http://localhost:8888/api/update_score
- Example of Submit Data
    ```JSON
    {
        "scoreId": 114,
        "userId": 1,
        "correctCount": 18,
        "errorCount": 6,
        "noAnswerCount": 3
    }
    ```
  
### [DELETE] Delete score by ID

- Base URI: http://localhost:8888/api/delete_score
- Parameter
  - id (Number)
- Example: http://localhost:8888/api/delete_score?id=114

### [DELETE] Truncate score table

- URL: http://localhost:8888/api/truncate_score
