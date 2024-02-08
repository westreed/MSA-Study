import logging
from enum import Enum

import requests
from py_eureka_client import eureka_client

g_redis_scheduling_map = {}


class StrEnum(str, Enum):
    def _generate_next_value_(name, start, count, last_values):
        return name

    def __repr__(self):
        return self.name

    def __str__(self):
        return self.name


class Scheduling(StrEnum):
    SEQUENCE = 1


class FeignProvider:

    def __init__(self, eureka_server, app_name, scheduling="sequence"):
        self.eureka_server = eureka_server
        self.app_name = app_name
        self.scheduling = scheduling.upper().replace("-", "_")

        global g_redis_scheduling_map
        if not g_redis_scheduling_map.get(app_name):
            g_redis_scheduling_map[app_name] = 0

    def __scheduling(self, size):
        global g_redis_scheduling_map
        cur_index = g_redis_scheduling_map[self.app_name]

        if self.scheduling == Scheduling.SEQUENCE:
            if size-1 > cur_index:
                cur_index += 1
            else:
                cur_index = 0
            g_redis_scheduling_map[self.app_name] = cur_index

        return cur_index

    async def __get_instances(self, index=-1):
        service_instances = await eureka_client.get_application(eureka_server=self.eureka_server, app_name=self.app_name)
        if len(service_instances.instances) <= index:
            raise IndexError("존재하지 않는 서버 인덱스입니다.")
        if index == -1:
            index = self.__scheduling(len(service_instances.instances))
        return service_instances.instances[index]

    @staticmethod
    def __create_target_url(instance, path):
        if path and path[0] == "/":
            i = 1
            while i < len(path) and path[i] == "/":
                i += 1

            path = path[i:]
        return f"http://{instance.ipAddr}:{instance.port.port}/{path}"

    async def a_get(self, path, index=-1):
        instance = await self.__get_instances(index)
        target_url = self.__create_target_url(instance, path)
        try:
            response = requests.get(target_url)
            return response
        except Exception as e:
            logging.error(f"GET {self.app_name}/{path} URI로 요청을 보내는 데 실패했습니다. {e}")

    async def a_post(self, path, index=0, **kwargs):
        instance = await self.__get_instances(index)
        target_url = self.__create_target_url(instance, path)
        try:
            response = requests.post(target_url, json=kwargs)
            return response
        except Exception as e:
            logging.error(f"POST {self.app_name}/{path} URI로 요청을 보내는 데 실패했습니다. {e}")
