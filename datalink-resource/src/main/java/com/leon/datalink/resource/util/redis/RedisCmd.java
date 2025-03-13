package com.leon.datalink.resource.util.redis;

import com.leon.datalink.core.utils.SignUtil;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

/**
 * redis命令执行工具
 *
 * @author leon
 * @date 2022年8月15日17:15:04
 */
public class RedisCmd {

    public static Object execute(Jedis jedis, String command) {
        Object result = null;
        try {
            RedisCommandEnum commandEnum = getCmd(command);

            switch (commandEnum) {
                case GET:
                case SET: {
                    result = string(jedis, command);
                    break;
                }
                case HGET:
                case HMGET:
                case HGETALL:
                case HKEYS:
                case HSET: {
                    result = hash(jedis, command);
                    break;
                }
                case LINDEX:
                case LLEN:
                case LRANGE:
                case LPUSH:
                case RPUSH: {
                    result = list(jedis, command);
                    break;
                }
                case SCARD:
                case SADD:
                case SMEMBERS:
                case SRANDMEMBER: {
                    result = set(jedis, command);
                    break;
                }
                case ZCARD:
                case ZSCORE:
                case ZCOUNT:
                case ZRANGE:
                case ZADD: {
                    result = zset(jedis, command);
                    break;
                }
                case TYPE: {
                    result = type(jedis, command);
                    break;
                }
                case DEL: {
                    result = del(jedis, command);
                    break;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Object string(Jedis jedis, String command) {
        String[] list = SignUtil.splitBySpace(command);
        RedisCommandEnum cmd = getCmd(command);
        String key = list[1];
        Object result = null;
        switch (cmd) {
            case GET: {
                result = jedis.get(key);
                break;
            }
            case SET: {
                result = jedis.set(key, list[2]);
                break;
            }
        }
        return result;
    }

    public static Object hash(Jedis jedis, String command) {
        String[] list = SignUtil.splitBySpace(command);
        RedisCommandEnum cmd = getCmd(command);
        String key = list[1];
        Object result = null;
        switch (cmd) {
            case HGETALL: {
                result = jedis.hgetAll(key);
                break;
            }
            case HGET: {
                result = jedis.hget(key, list[2]);
                break;
            }
            case HMGET: {
                String[] items = removeCommandAndKey(list);
                result = jedis.hmget(key, items);
                break;
            }
            case HKEYS: {
                result = jedis.hkeys(key);
                break;
            }
            case HSET: {
                Map<String, String> hash = new HashMap<>();
                String[] items = removeCommandAndKey(list);
                for (int i = 0; i < items.length; i += 2) {
                    hash.put(items[i], items[i + 1]);
                }
                result = jedis.hset(key, hash);
                break;
            }
        }
        return result;
    }

    public static Object list(Jedis jedis, String command) {
        String[] list = SignUtil.splitBySpace(command);
        RedisCommandEnum cmd = getCmd(command);
        String key = list[1];
        String[] items = removeCommandAndKey(list);
        Object result = null;
        switch (cmd) {
            case LPUSH: {
                result = jedis.lpush(key, items);
                break;
            }
            case RPUSH: {
                result = jedis.rpush(key, items);
                break;
            }
            case LINDEX: {
                result = jedis.lindex(key, Integer.parseInt(list[2]));
                break;
            }
            case LLEN: {
                result = jedis.llen(key);
                break;
            }
            case LRANGE: {
                int start = Integer.parseInt(list[2]);
                int stop = Integer.parseInt(list[3]);
                result = jedis.lrange(key, start, stop);
                break;
            }
        }
        return result;
    }

    public static Object set(Jedis jedis, String command) {
        String[] list = SignUtil.splitBySpace(command);
        RedisCommandEnum cmd = getCmd(command);
        String key = list[1];
        Object result = null;
        switch (cmd) {
            case SCARD: {
                result = jedis.scard(key);
                break;
            }
            case SADD: {
                result = jedis.sadd(key, removeCommandAndKey(list));
                break;
            }
            case SMEMBERS: {
                result = jedis.smembers(key);
                break;
            }
            case SRANDMEMBER: {
                int count = 1;
                if (list.length > 2) {
                    count = Integer.parseInt(list[2]);
                }
                result = jedis.srandmember(key, count);
                break;
            }
        }
        return result;
    }

    public static Object zset(Jedis jedis, String command) {
        String[] list = SignUtil.splitBySpace(command);
        RedisCommandEnum cmd = getCmd(command);
        String key = list[1];
        String param1 = list[2];
        String param2 = list[3];
        Object result = null;
        switch (cmd) {
            case ZCARD: {
                result = jedis.zcard(key);
                break;
            }
            case ZSCORE: {
                result = jedis.zscore(key, param1);
                break;
            }
            case ZCOUNT: {
                result = jedis.zcount(key, param1, param2);
                break;
            }
            case ZRANGE: {
                int start = Integer.parseInt(param1);
                int stop = Integer.parseInt(param2);
                if (list.length > 4) {
                    result = jedis.zrangeWithScores(key, start, stop);
                } else {
                    result = jedis.zrange(key, start, stop);
                }
                break;
            }
            case ZADD: {
                result = jedis.zadd(key, Double.parseDouble(param1), param2);
                break;
            }
        }
        return result;
    }


    public static Object type(Jedis jedis, String command) {
        String key = getKey(command);
        return jedis.type(key);
    }


    public static Object del(Jedis jedis, String command) {
        String key = getKey(command);
        return jedis.del(key);
    }




    private static String[] removeCommandAndKey(String[] list) {
        int length = list.length;
        String[] items = new String[length - 2];
        for (int i = 2, j = 0; i < length; i++, j++) {
            items[j] = list[i];
        }
        return items;
    }

    private static String getKey(String command) {
        return SignUtil.splitBySpace(command)[1];
    }

    private static RedisCommandEnum getCmd(String command) {
        return RedisCommandEnum.valueOf(SignUtil.splitBySpace(command)[0].toUpperCase());
    }


}
