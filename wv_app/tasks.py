
import logging
from celery import shared_task
from celery.result import AsyncResult
from celery.contrib.abortable import AbortableAsyncResult
from .learn import learning

logger = logging.getLogger('my')
#celery worker function

@shared_task(track_started=True)
def start_learning(data):
    siteNo = str(data['siteNo'])
    run = learning.learn('siteNo_'+siteNo)
    return run.run(data)
   
def celery_state(task_id):
  task = AsyncResult(id=str(task_id))
  return {'state':task.state, 'worker_id' : task_id}

def celery_stop(task_id):
  try: 
    task = AsyncResult(id=str(task_id))
    if task.state == 'ABORTED': 
        return {'code' : '401', 'message' : '이미 중단된 작업 입니다.'}
    elif task.state == 'PENDING':
        return {'code' : '402', 'message' : '실행중인 작업이 아닙니다.'}
    AbortableAsyncResult(task_id).abort()
    AsyncResult(id=task_id).revoke(terminate=True, signal="SIGKILL")
    logger.info("task kill")
    return {'code' : '200', 'message' : '중단 성공'}
  except Exception as e:
    return {'code' : '499', 'message' : str(e)}
  