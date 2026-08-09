import http from 'k6/http';
import { check } from 'k6';

const baseUrl = __ENV.BASE_URL || 'http://localhost:8080';
const postId = __ENV.POST_ID || '1';
const vus = Number(__ENV.VUS || 1000);

export const options = {
  discardResponseBodies: true,
  scenarios: {
    differentViewers: {
      executor: 'per-vu-iterations',
      vus,
      iterations: 1,
      maxDuration: '2m',
    },
  },
  thresholds: {
    checks: ['rate==1'],
    http_req_failed: ['rate==0'],
  },
};

function guestIdFor(vuNumber) {
  // UUID 형식을 유지하면서 VU마다 겹치지 않는 비회원 식별자를 만든다.
  const suffix = String(vuNumber).padStart(12, '0');
  return `00000000-0000-4000-8000-${suffix}`;
}

export default function () {
  const guestId = guestIdFor(__VU);

  const response = http.get(`${baseUrl}/api/v1/posts/${postId}`, {
    headers: {
      Cookie: `guest_id=${guestId}`,
    },
    tags: {
      test_case: 'different-viewers',
    },
  });

  check(response, {
    '상세 조회가 200으로 응답한다': (res) => res.status === 200,
  });
}
