import http from 'k6/http';
import { check } from 'k6';

const baseUrl = __ENV.BASE_URL || 'http://localhost:8080';
const postId = __ENV.POST_ID || '1';
const testCase = __ENV.TEST_CASE || 'same-viewer';
const vus = Number(__ENV.VUS || (testCase === 'same-viewer' ? 100 : 1000));

if (!['same-viewer', 'different-viewers'].includes(testCase)) {
  throw new Error(`지원하지 않는 TEST_CASE입니다: ${testCase}`);
}

export const options = {
  scenarios: {
    correctness: {
      executor: 'per-vu-iterations',
      vus,
      iterations: 1,
      maxDuration: '1m',
    },
  },
  thresholds: {
    checks: ['rate==1'],
    http_req_failed: ['rate==0'],
  },
};

function guestIdFor(vuNumber) {
  if (testCase === 'same-viewer') {
    return '11111111-1111-4111-8111-111111111111';
  }

  // 서버의 UUID 유효성 검사를 통과하면서 VU마다 서로 다른 식별자를 만든다.
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
      test_case: testCase,
    },
  });

  check(response, {
    '상세 조회가 200으로 응답한다': (res) => res.status === 200,
  });
}
