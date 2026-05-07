import { useEffect } from 'react';
import { Client } from '@stomp/stompjs';
import { useQueryClient } from '@tanstack/react-query';

const WS_URL = 'ws://localhost:8080/ws';

type Subscription = {
  topic: string;
  invalidate: unknown[][];
};

export function useRealtimeSync(subscriptions: Subscription[]) {
  const qc = useQueryClient();

  useEffect(() => {
    const client = new Client({
      brokerURL: WS_URL,
      reconnectDelay: 5000,
      onConnect: () => {
        subscriptions.forEach(({ topic, invalidate }) => {
          client.subscribe(`/topic/${topic}`, () => {
            invalidate.forEach(key => {
              qc.invalidateQueries({ queryKey: key as string[] });
            });
          });
        });
      },
    });

    client.activate();
    return () => { client.deactivate(); };
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);
}
